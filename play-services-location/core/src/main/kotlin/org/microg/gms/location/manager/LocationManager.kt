/*
 * SPDX-FileCopyrightText: 2023 microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */

package org.microg.gms.location.manager

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.*
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import androidx.core.app.PendingIntentCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.location.LocationRequestCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.google.android.gms.location.Granularity.GRANULARITY_COARSE
import com.google.android.gms.location.Granularity.GRANULARITY_FINE
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.internal.ClientIdentity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.microg.gms.location.*
import org.microg.gms.location.core.BuildConfig
import org.microg.gms.utils.IntentCacheManager
import java.io.PrintWriter
import kotlin.math.max
import kotlin.math.min
import android.location.LocationManager as SystemLocationManager

class LocationManager(private val context: Context, override val lifecycle: Lifecycle) : LifecycleOwner {
    // 延迟初始化各种位置管理相关的组件
    private var coarsePendingIntent: PendingIntent? = null
    private val postProcessor by lazy { LocationPostProcessor() }  // 位置后处理器
    private val lastLocationCapsule by lazy { LastLocationCapsule(context) }  // 最后位置的容器
    val database by lazy { LocationAppsDatabase(context) }  // 位置相关的数据库访问
    private val requestManager by lazy { LocationRequestManager(context, lifecycle, postProcessor, database) { onRequestManagerUpdated() } }  // 请求管理器
    private val gpsLocationListener by lazy { LocationListenerCompat { updateGpsLocation(it) } }  // GPS位置监听器
    private val networkLocationListener by lazy { LocationListenerCompat { updateNetworkLocation(it) } }  // 网络位置监听器
    private var boundToSystemNetworkLocation: Boolean = false  // 是否绑定到系统网络位置服务
    private val activePermissionRequestLock = Mutex()  // 权限请求的锁
    private var activePermissionRequest: Deferred<Boolean>? = null  // 活跃的权限请求

    val deviceOrientationManager = DeviceOrientationManager(context, lifecycle)  // 设备方向管理器

    var started: Boolean = false
        private set  // 标记位置管理器是否已经启动

    // 根据客户端身份和位置请求信息，获取最后的位置数据
    suspend fun getLastLocation(clientIdentity: ClientIdentity, request: LastLocationRequest): Location? {
        // 如果请求的最大更新年龄小于0，抛出非法参数异常
        if (request.maxUpdateAgeMillis < 0) throw IllegalArgumentException()
        // 检查请求的精度是否有效
        GranularityUtil.checkValidGranularity(request.granularity)
        // 如果请求中包含绕过权限的要求
        if (request.isBypass) {
            // 根据系统版本，决定所需的权限
            val permission = if (SDK_INT >= 33) "android.permission.LOCATION_BYPASS" else Manifest.permission.WRITE_SECURE_SETTINGS
            // 检查调用方是否具有相应的权限
            if (context.checkPermission(permission, clientIdentity.pid, clientIdentity.uid) != PackageManager.PERMISSION_GRANTED) {
                throw SecurityException("Caller must hold $permission for location bypass")
            }
        }
        // 如果存在冒充请求
        if (request.impersonation != null) {
            // 记录警告信息
            Log.w(TAG, "${clientIdentity.packageName} wants to impersonate ${request.impersonation!!.packageName}. Ignoring.")
        }
        // 获取调用者根据权限所能获取的最高精度
        val permissionGranularity = context.granularityFromPermission(clientIdentity)
        // 计算有效精度，可能会受到强制粗略位置的影响
        var effectiveGranularity = getEffectiveGranularity(request.granularity, permissionGranularity)
        if (effectiveGranularity == GRANULARITY_FINE && database.getForceCoarse(clientIdentity.packageName)) effectiveGranularity = GRANULARITY_COARSE
        // 根据有效精度获取位置信息
        val returnedLocation = if (effectiveGranularity > permissionGranularity) {
            // 如果请求的精度超出了权限范围，返回 null
            null
        } else {
            // 确保有必要的权限
            ensurePermissions()
            // 获取位置信息
            val preLocation = lastLocationCapsule.getLocation(effectiveGranularity, request.maxUpdateAgeMillis)
            // 处理获取到的位置信息
            val processedLocation = postProcessor.process(preLocation, effectiveGranularity, clientIdentity.isGoogle(context))
            // 检查是否有操作权限
            if (!context.noteAppOpForEffectiveGranularity(clientIdentity, effectiveGranularity)) {
                // 如果操作权限被拒绝，返回 null
                null
            } else if (processedLocation != null && clientIdentity.isSelfProcess()) {
                // 如果请求来自本进程，确保返回新的 Location 对象以防止修改内部状态
                Location(processedLocation)
            } else {
                // 否则，返回处理过的位置信息
                processedLocation
            }
        }
        // 记录获取的位置信息
        database.noteAppLocation(clientIdentity.packageName, returnedLocation)
        // 如果有返回位置，确保设置正确的提供者标识，并返回新的 Location 对象
        return returnedLocation?.let { Location(it).apply { provider = "fused" } }
    }


    // 获取位置可用性信息
    fun getLocationAvailability(clientIdentity: ClientIdentity, request: LocationAvailabilityRequest): LocationAvailability {
        // 如果请求需要绕过权限验证
        if (request.bypass) {
            // 根据系统版本确定所需权限
            val permission = if (SDK_INT >= 33) "android.permission.LOCATION_BYPASS" else Manifest.permission.WRITE_SECURE_SETTINGS
            // 检查调用者是否具有该权限
            if (context.checkPermission(permission, clientIdentity.pid, clientIdentity.uid) != PackageManager.PERMISSION_GRANTED) {
                throw SecurityException("Caller must hold $permission for location bypass")
            }
        }
        // 如果请求包含冒充其他应用的信息
        if (request.impersonation != null) {
            // 记录警告日志，忽略冒充行为
            Log.w(TAG, "${clientIdentity.packageName} wants to impersonate ${request.impersonation!!.packageName}. Ignoring.")
        }
        // 返回位置可用性信息
        return lastLocationCapsule.locationAvailability
    }

    // 添加基于Binder通信的位置请求
    suspend fun addBinderRequest(clientIdentity: ClientIdentity, binder: IBinder, callback: ILocationCallback, request: LocationRequest) {
        // 验证请求合法性
        request.verify(context, clientIdentity)
        // 确保有足够权限
        ensurePermissions()
        // 向位置请求管理器添加请求
        requestManager.add(binder, clientIdentity, callback, request, lastLocationCapsule)
    }

    // 更新基于Binder通信的位置请求
    suspend fun updateBinderRequest(
        clientIdentity: ClientIdentity,
        oldBinder: IBinder,
        binder: IBinder,
        callback: ILocationCallback,
        request: LocationRequest
    ) {
        // 验证请求合法性
        request.verify(context, clientIdentity)
        // 向位置请求管理器更新请求
        requestManager.update(oldBinder, binder, clientIdentity, callback, request, lastLocationCapsule)
    }

    // 移除基于Binder通信的位置请求
    suspend fun removeBinderRequest(binder: IBinder) {
        // 从位置请求管理器中移除请求
        requestManager.remove(binder)
    }

    // 添加基于Intent的位置请求
    suspend fun addIntentRequest(clientIdentity: ClientIdentity, pendingIntent: PendingIntent, request: LocationRequest) {
        // 验证请求合法性
        request.verify(context, clientIdentity)
        // 确保有足够权限
        ensurePermissions()
        // 向位置请求管理器添加请求
        requestManager.add(pendingIntent, clientIdentity, request, lastLocationCapsule)
    }

    // 移除基于Intent的位置请求
    suspend fun removeIntentRequest(pendingIntent: PendingIntent) {
        // 从位置请求管理器中移除请求
        requestManager.remove(pendingIntent)
    }


    //启动位置管理器
    fun start() {
        // 使用 synchronized 确保在多线程环境中对 started 变量的检查和修改是线程安全的
        synchronized(this) {
            // 如果服务已经启动，则直接返回，防止重复启动
            if (started) return
            // 标记服务为已启动
            started = true
        }
        // 创建一个指向 LocationManagerService 的 Intent
        val intent = Intent(context, LocationManagerService::class.java)
        // 设置 Intent 的操作为报告位置
        intent.action = LocationManagerService.ACTION_REPORT_LOCATION
        // 创建一个用于启动服务的 PendingIntent，这允许外部应用在具有相应权限的情况下触发位置更新
        coarsePendingIntent = PendingIntentCompat.getService(
            context, 0, intent, FLAG_UPDATE_CURRENT, true
        )
        // 启动 lastLocationCapsule，负责位置信息的获取和管理
        lastLocationCapsule.start()
        // 启动位置请求管理器，负责处理位置更新请求
        requestManager.start()
    }


    //停止位置管理器
    fun stop() {
        // 使用 synchronized 确保对 started 变量的访问是线程安全的
        synchronized(this) {
            // 如果服务未启动，则直接返回
            if (!started) return
            // 设置 started 标志为 false，表示服务已停止
            started = false
        }
        // 停止位置请求管理器的活动
        requestManager.stop()
        // 停止位置信息存储器的活动
        lastLocationCapsule.stop()
        // 停止设备方向管理器的活动
        deviceOrientationManager.stop()

        // 如果设备内置了网络位置服务
        if (context.hasNetworkLocationServiceBuiltIn()) {
            // 创建意图以停止网络位置服务
            val intent = Intent(ACTION_NETWORK_LOCATION_SERVICE)
            intent.`package` = context.packageName  // 指定包名
            intent.putExtra(EXTRA_PENDING_INTENT, coarsePendingIntent)  // 传递待处理的意图
            intent.putExtra(EXTRA_ENABLE, false)  // 通过 EXTRA_ENABLE 设置为 false 来停止服务
            context.startService(intent)  // 启动服务
        }

        // 获取系统位置管理器
        val locationManager = context.getSystemService<SystemLocationManager>() ?: return  // 如果未能获取则直接返回
        try {
            // 如果已经绑定到系统网络位置
            if (boundToSystemNetworkLocation) {
                // 移除网络位置更新监听
                LocationManagerCompat.removeUpdates(locationManager, networkLocationListener)
                // 更新绑定状态
                boundToSystemNetworkLocation = false
            }
            // 移除 GPS 位置更新监听
            LocationManagerCompat.removeUpdates(locationManager, gpsLocationListener)
        } catch (e: SecurityException) {
            // 忽略由于权限问题引起的 SecurityException，防止程序因异常而崩溃
        }
    }


    // 请求管理器更新回调函数
    private fun onRequestManagerUpdated() {
        // 根据请求管理器中的精度要求设置网络位置更新的间隔
        val networkInterval = when (requestManager.granularity) {
            GRANULARITY_COARSE -> max(requestManager.intervalMillis, MAX_COARSE_UPDATE_INTERVAL)
            GRANULARITY_FINE -> max(requestManager.intervalMillis, MAX_FINE_UPDATE_INTERVAL)
            else -> Long.MAX_VALUE  // 默认设置，无更新
        }
        // 根据请求管理器中的优先级和精度要求设置 GPS 更新的间隔
        val gpsInterval = when (requestManager.priority to requestManager.granularity) {
            PRIORITY_HIGH_ACCURACY to GRANULARITY_FINE -> requestManager.intervalMillis  // 高精度且细粒度时使用设定间隔
            else -> Long.MAX_VALUE  // 默认设置，无更新
        }

        // 如果设备内置了网络位置服务
        if (context.hasNetworkLocationServiceBuiltIn()) {
            // 创建一个针对网络位置服务的意图
            val intent = Intent(ACTION_NETWORK_LOCATION_SERVICE)
            intent.`package` = context.packageName  // 设置包名
            intent.putExtra(EXTRA_PENDING_INTENT, coarsePendingIntent)  // 传递一个待处理的意图
            intent.putExtra(EXTRA_ENABLE, true)  // 启用服务
            intent.putExtra(EXTRA_INTERVAL_MILLIS, networkInterval)  // 设置更新间隔
            intent.putExtra(EXTRA_LOW_POWER, requestManager.granularity <= GRANULARITY_COARSE || requestManager.priority >= Priority.PRIORITY_LOW_POWER)  // 设置低功耗模式
            intent.putExtra(EXTRA_WORK_SOURCE, requestManager.workSource)  // 设置工作来源
            context.startService(intent)  // 启动服务
        }

        // 获取系统位置管理器服务
        val locationManager = context.getSystemService<SystemLocationManager>() ?: return  // 如果未获取到则直接返回
        // 请求 GPS 位置更新
        locationManager.requestSystemProviderUpdates(SystemLocationManager.GPS_PROVIDER, gpsInterval, gpsLocationListener)
        // 如果设备没有内置网络位置服务且网络位置提供者可用
        if (!context.hasNetworkLocationServiceBuiltIn() && LocationManagerCompat.hasProvider(locationManager, SystemLocationManager.NETWORK_PROVIDER)) {
            boundToSystemNetworkLocation = true  // 标记已绑定到系统网络位置
            // 请求网络位置更新
            locationManager.requestSystemProviderUpdates(SystemLocationManager.NETWORK_PROVIDER, networkInterval, networkLocationListener)
        }
    }


    private fun SystemLocationManager.requestSystemProviderUpdates(provider: String, interval: Long, listener: LocationListenerCompat) {
        try {
            // 检查是否设置了具体的更新间隔
            if (interval != Long.MAX_VALUE) {
                // 如果设置了具体的更新间隔，则按照这个间隔请求位置更新
                LocationManagerCompat.requestLocationUpdates(
                    this,
                    provider,
                    LocationRequestCompat.Builder(interval).build(),
                    listener,
                    context.mainLooper
                )
            } else {
                // 如果更新间隔为 Long.MAX_VALUE，视为使用被动间隔（PASSIVE_INTERVAL）
                // 设置被动间隔的最小更新时间
                LocationManagerCompat.requestLocationUpdates(
                    this,
                    provider,
                    LocationRequestCompat.Builder(LocationRequestCompat.PASSIVE_INTERVAL)
                        .setMinUpdateIntervalMillis(MAX_FINE_UPDATE_INTERVAL).build(),
                    listener,
                    context.mainLooper
                )
            }
        } catch (e: SecurityException) {
            // 忽略 SecurityException，可能是因为没有足够的权限请求位置更新
            // 在这里可以处理日志记录或者用户提示，当前选择忽略
        } catch (e: Exception) {
            // 忽略其他类型的异常，保证程序的稳定性
            // 同样可以在这里增加错误处理逻辑
        }
    }

    // 更新网络位置
    fun updateNetworkLocation(location: Location) {
        // 从 lastLocationCapsule 获取当前保存的最新精确位置
        val lastLocation = lastLocationCapsule.getLocation(GRANULARITY_FINE, Long.MAX_VALUE)

        // 如果获取到的位置信息比当前已有的位置信息更旧，则忽略此次更新
        if (lastLocation != null && location.elapsedMillis + UPDATE_CLIFF_MS < lastLocation.elapsedMillis) return

        // 检查是否应更新位置：
        // 1. 如果没有已知的最后位置；
        // 2. 新位置的精确度比已知位置的精确度更高；
        // 3. 新位置的时间戳显著晚于已知位置的时间戳；
        // 4. 随着时间推移，新位置相较于旧位置显示了更好的精确度。
        if (lastLocation == null ||
            lastLocation.accuracy > location.accuracy ||
            lastLocation.elapsedMillis + min(requestManager.intervalMillis * 2, UPDATE_CLIFF_MS) < location.elapsedMillis ||
            lastLocation.accuracy + ((location.elapsedMillis - lastLocation.elapsedMillis) / 1000.0) > location.accuracy
        ) {
            // 更新 lastLocationCapsule 中的粗略位置信息
            lastLocationCapsule.updateCoarseLocation(location)
            // 调用 sendNewLocation 函数来处理并发送更新后的位置信息
            sendNewLocation()
        }
    }


    // 更新GPS位置
    fun updateGpsLocation(location: Location) {
        // 使用传入的 Location 对象更新 lastLocationCapsule 中的精确位置信息
        lastLocationCapsule.updateFineLocation(location)
        // 调用 sendNewLocation 函数来处理并发送更新后的位置信息
        sendNewLocation()
    }


    // 发送新位置
    fun sendNewLocation() {
        // 使用 lifecycleScope 和 launchWhenStarted 确保只在 LifecycleOwner（如 Activity 或 Fragment）处于 STARTED 状态时执行
        lifecycleScope.launchWhenStarted {
            // 请求管理器处理最新的位置数据，这里假定 processNewLocation 方法内部处理了位置数据的更新逻辑
            requestManager.processNewLocation(lastLocationCapsule)
        }
        // 获取精确的位置数据，如果位置数据存在，则执行 let 代码块
        lastLocationCapsule.getLocation(GRANULARITY_FINE, Long.MAX_VALUE)?.let { location ->
            // 当位置发生变化时，通知设备方向管理器更新，这里传递的是新获取的位置
            deviceOrientationManager.onLocationChanged(location)
        }
    }


    // 确保有正确的权限
    private suspend fun ensurePermissions(): Boolean {
        // 如果 Android SDK 版本低于 23（Android 6.0），直接返回 true，因为低版本无需运行时权限请求
        if (SDK_INT < 23)
            return true

        // 初始化权限列表，包括粗略位置和精确位置权限
        val permissions = mutableListOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        // 如果 Android SDK 版本大于等于 29（Android 10），添加后台位置访问权限
        if (SDK_INT >= 29) permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

        // 检查所有需要的权限是否已经被授予
        if (permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED })
            return true  // 如果所有权限都已授予，返回 true

        // 如果定义了 FORCE_SHOW_BACKGROUND_PERMISSION 并且其值非空，添加这个特定的权限到请求列表
        if (BuildConfig.FORCE_SHOW_BACKGROUND_PERMISSION.isNotEmpty()) permissions.add(BuildConfig.FORCE_SHOW_BACKGROUND_PERMISSION)

        // 调用之前定义的 requestPermission 函数请求未授予的权限，并返回其结果
        return requestPermission(permissions)
    }


    // 请求权限
    private suspend fun requestPermission(permissions: List<String>): Boolean {
        // 使用锁机制确保同一时刻只有一个权限请求在进行
        val (completable, deferred) = activePermissionRequestLock.withLock {
            if (activePermissionRequest == null) {
                // 如果当前没有正在进行的权限请求，创建一个新的CompletableDeferred<Boolean>
                val completable = CompletableDeferred<Boolean>()
                activePermissionRequest = completable
                completable to activePermissionRequest!!
            } else {
                // 如果已有正在进行的请求，重用现有的deferred对象
                null to activePermissionRequest!!
            }
        }
        // 如果completable不为null，说明这是一个新的权限请求
        if (completable != null) {
            // 创建一个跳转到 AskPermissionActivity 的 Intent
            val intent = Intent(context, AskPermissionActivity::class.java)
            // 附加一个 Messenger，用于从 AskPermissionActivity 接收结果
            intent.putExtra(EXTRA_MESSENGER, Messenger(object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    // 处理从权限请求Activity返回的结果
                    if (msg.what == Activity.RESULT_OK) {
                        // 如果用户授予了权限，从系统中获取最新位置信息
                        lastLocationCapsule.fetchFromSystem()
                        onRequestManagerUpdated()
                        // 检查所有请求的权限是否都被授予
                        val grantResults = msg.data?.getIntArray(EXTRA_GRANT_RESULTS) ?: IntArray(0)
                        completable.complete(grantResults.size == permissions.size && grantResults.all { it == PackageManager.PERMISSION_GRANTED })
                    } else {
                        // 如果用户未授予权限或发生其他错误，返回false
                        completable.complete(false)
                    }
                }
            }))
            // 传递权限列表到 AskPermissionActivity
            intent.putExtra(EXTRA_PERMISSIONS, permissions.toTypedArray())
            // 设置标志以新任务形式启动 Activity
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            // 启动 AskPermissionActivity
            context.startActivity(intent)
        }
        // 等待并返回权限请求结果
        return deferred.await()
    }


    // 输出调试信息
    fun dump(writer: PrintWriter) {
        // 打印位置可用性信息
        writer.println("Location availability: ${lastLocationCapsule.locationAvailability}")
        // 打印最后的粗略位置信息，经过后处理
        writer.println("Last coarse location: ${postProcessor.process(lastLocationCapsule.getLocation(GRANULARITY_COARSE, Long.MAX_VALUE), GRANULARITY_COARSE, true)}")
        // 打印最后的精确位置信息，经过后处理
        writer.println("Last fine location: ${postProcessor.process(lastLocationCapsule.getLocation(GRANULARITY_FINE, Long.MAX_VALUE), GRANULARITY_FINE, true)}")
        // 打印网络位置服务信息，包括是否内建和系统服务绑定情况
        writer.println("Network location: built-in=${context.hasNetworkLocationServiceBuiltIn()} system=$boundToSystemNetworkLocation")
        // 输出请求管理器的详细信息
        requestManager.dump(writer)
        // 输出设备方向管理器的详细信息
        deviceOrientationManager.dump(writer)
    }


    // 处理缓存的Intent
    fun handleCacheIntent(intent: Intent) {
        when (IntentCacheManager.getType(intent)) {
            LocationRequestManager.CACHE_TYPE -> {
                requestManager.handleCacheIntent(intent)
            }

            else -> {
                Log.w(TAG, "Unknown cache intent: $intent")
            }
        }
    }

    companion object {
        // 定义一些时间常数
        const val MAX_COARSE_UPDATE_INTERVAL = 20_000L
        const val MAX_FINE_UPDATE_INTERVAL = 10_000L
        const val EXTENSION_CLIFF_MS = 10_000L
        const val UPDATE_CLIFF_MS = 30_000L
    }
}