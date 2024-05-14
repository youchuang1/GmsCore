/*
 * SPDX-FileCopyrightText: 2023 microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */

package org.microg.gms.location.manager

import android.Manifest.permission.*
import android.app.PendingIntent
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.Binder
import android.os.Build.VERSION.SDK_INT
import android.os.IBinder
import android.os.Parcel
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import androidx.core.app.PendingIntentCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.gms.common.api.internal.IStatusCallback
import com.google.android.gms.common.internal.ICancelToken
import com.google.android.gms.common.internal.safeparcel.SafeParcelableSerializer
import com.google.android.gms.location.*
import com.google.android.gms.location.internal.*
import com.google.android.gms.location.internal.DeviceOrientationRequestUpdateData.REMOVE_UPDATES
import com.google.android.gms.location.internal.DeviceOrientationRequestUpdateData.REQUEST_UPDATES
import kotlinx.coroutines.*
import org.microg.gms.location.hasNetworkLocationServiceBuiltIn
import org.microg.gms.location.settings.*
import org.microg.gms.utils.warnOnTransactionIssues

// LocationManagerInstance 类定义，用于管理位置服务的实例
class LocationManagerInstance(
    // 传入的 Context 对象，用于访问应用级别的服务和资源
    private val context: Context,
    // 传入的 LocationManager 对象，用于管理设备的位置服务
    private val locationManager: LocationManager,
    // 调用此类的包名，用于可能的权限检查或日志记录
    private val packageName: String,
    // 继承 LifecycleOwner 接口的 lifecycle 属性，允许这个类响应外部生命周期事件
    override val lifecycle: Lifecycle
) :

    AbstractLocationManagerInstance(), LifecycleOwner {


    // 添加地理围栏的方法
    override fun addGeofences(geofencingRequest: GeofencingRequest?, pendingIntent: PendingIntent?, callbacks: IGeofencerCallbacks?) {
        // 记录调试日志，表示此方法尚未实现，并记录调用者的包名
        Log.d(TAG, "Not yet implemented: addGeofences by ${getClientIdentity().packageName}")
    }

    // 通过 Intent 移除地理围栏的方法
    override fun removeGeofencesByIntent(pendingIntent: PendingIntent?, callbacks: IGeofencerCallbacks?, packageName: String?) {
        // 记录调试日志，表示此方法尚未实现，并记录调用者的包名
        Log.d(TAG, "Not yet implemented: removeGeofencesByIntent by ${getClientIdentity().packageName}")
    }

    // 通过围栏 ID 移除地理围栏的方法
    override fun removeGeofencesById(geofenceRequestIds: Array<out String>?, callbacks: IGeofencerCallbacks?, packageName: String?) {
        // 记录调试日志，表示此方法尚未实现，并记录调用者的包名
        Log.d(TAG, "Not yet implemented: removeGeofencesById by ${getClientIdentity().packageName}")
    }

    // 移除所有地理围栏的方法
    override fun removeAllGeofences(callbacks: IGeofencerCallbacks?, packageName: String?) {
        // 记录调试日志，表示此方法尚未实现，并记录调用者的包名
        Log.d(TAG, "Not yet implemented: removeAllGeofences by ${getClientIdentity().packageName}")
    }

    // 获取上一次识别的活动
    override fun getLastActivity(packageName: String?): ActivityRecognitionResult {
        // 记录日志，指出此方法尚未实现，并显示调用者的包名
        Log.d(TAG, "Not yet implemented: getLastActivity by ${getClientIdentity().packageName}")
        // 返回一个包含未知活动的结果
        return ActivityRecognitionResult(
            listOf(DetectedActivity(DetectedActivity.UNKNOWN, 0)), // 包含一个未知类型的活动和置信度0
            System.currentTimeMillis(), // 设置结果的时间戳为当前系统时间
            SystemClock.elapsedRealtime() // 设置结果的相对时间戳
        )
    }

    // 请求活动转换更新
    override fun requestActivityTransitionUpdates(request: ActivityTransitionRequest?, pendingIntent: PendingIntent?, callback: IStatusCallback?) {
        // 记录日志，指出此方法尚未实现，并显示调用者的包名
        Log.d(TAG, "Not yet implemented: requestActivityTransitionUpdates by ${getClientIdentity().packageName}")
        // 调用回调函数，返回操作成功的状态
        callback?.onResult(Status.SUCCESS)
    }

    // 移除活动转换更新
    override fun removeActivityTransitionUpdates(pendingIntent: PendingIntent?, callback: IStatusCallback?) {
        // 记录日志，指出此方法尚未实现，并显示调用者的包名
        Log.d(TAG, "Not yet implemented: removeActivityTransitionUpdates by ${getClientIdentity().packageName}")
        // 调用回调函数，返回操作成功的状态
        callback?.onResult(Status.SUCCESS)
    }

    // 请求活动更新并使用回调
    override fun requestActivityUpdatesWithCallback(request: ActivityRecognitionRequest?, pendingIntent: PendingIntent?, callback: IStatusCallback?) {
        // 记录日志，指出此方法尚未实现，并显示调用者的包名
        Log.d(TAG, "Not yet implemented: requestActivityUpdatesWithCallback by ${getClientIdentity().packageName}")
        // 调用回调函数，返回操作成功的状态
        callback?.onResult(Status.SUCCESS)
    }

    // 移除活动更新
    override fun removeActivityUpdates(callbackIntent: PendingIntent?) {
        // 记录日志，指出此方法尚未实现，并显示调用者的包名
        Log.d(TAG, "Not yet implemented: removeActivityUpdates by ${getClientIdentity().packageName}")
    }

    // 移除睡眠段更新请求
    override fun removeSleepSegmentUpdates(pendingIntent: PendingIntent?, callback: IStatusCallback?) {
        // 记录日志，表明此功能尚未实现，同时记录调用者的包名
        Log.d(TAG, "Not yet implemented: removeSleepSegmentUpdates by ${getClientIdentity().packageName}")
        // 调用回调函数，返回操作成功的状态
        callback?.onResult(Status.SUCCESS)
    }

    // 请求睡眠段更新
    override fun requestSleepSegmentUpdates(pendingIntent: PendingIntent?, request: SleepSegmentRequest?, callback: IStatusCallback?) {
        // 记录日志，表明此功能尚未实现，同时记录调用者的包名
        Log.d(TAG, "Not yet implemented: requestSleepSegmentUpdates by ${getClientIdentity().packageName}")
        // 调用回调函数，返回操作成功的状态
        callback?.onResult(Status.SUCCESS)
    }


    // 刷新当前缓存的所有位置数据
    override fun flushLocations(callback: IFusedLocationProviderCallback?) {
        // 记录调试日志，显示调用此方法的应用包名
        Log.d(TAG, "flushLocations by ${getClientIdentity().packageName}")
        // 检查是否具有获取位置信息的权限
        checkHasAnyLocationPermission()
        // 记录一个日志说明此功能尚未实现
        Log.d(TAG, "Not yet implemented: flushLocations")
    }

    // 获取位置可用性信息，并通过接收器返回
    override fun getLocationAvailabilityWithReceiver(request: LocationAvailabilityRequest, receiver: LocationReceiver) {
        // 记录调试日志，显示调用此方法的应用包名
        Log.d(TAG, "getLocationAvailabilityWithReceiver by ${getClientIdentity().packageName}")
        // 检查是否具有获取位置信息的权限
        checkHasAnyLocationPermission()
        // 获取接收器中定义的回调接口
        val callback = receiver.availabilityStatusCallback
        // 获取调用者的客户端身份信息
        val clientIdentity = getClientIdentity()
        // 在组件的生命周期内启动协程，确保在组件活跃时执行
        lifecycleScope.launchWhenStarted {
            try {
                // 尝试通过位置管理器获取位置可用性并调用回调函数返回结果
                callback.onLocationAvailabilityStatus(Status.SUCCESS, locationManager.getLocationAvailability(clientIdentity, request))
            } catch (e: Exception) {
                try {
                    // 如果获取位置可用性时发生异常，返回错误状态和位置不可用的信息
                    callback.onLocationAvailabilityStatus(Status(CommonStatusCodes.ERROR, e.message), LocationAvailability.UNAVAILABLE)
                } catch (e2: Exception) {
                    // 如果在处理异常时发生另一个异常，记录警告日志
                    Log.w(TAG, "Failed", e)
                }
            }
        }
    }


    // 通过接收器获取当前位置信息并返回一个取消令牌
    override fun getCurrentLocationWithReceiver(request: CurrentLocationRequest, receiver: LocationReceiver): ICancelToken {
        // 记录调试信息，包括调用者的包名
        Log.d(TAG, "getCurrentLocationWithReceiver by ${getClientIdentity().packageName}")
        // 检查调用者是否具有任何形式的位置权限
        checkHasAnyLocationPermission()
        // 初始化返回状态标记
        var returned = false
        // 获取接收器的状态回调接口
        val callback = receiver.statusCallback
        // 获取调用者身份
        val clientIdentity = getClientIdentity()
        // 创建一个 Binder 实例来标识这次请求
        val binderIdentity = Binder()
        // 在组件的生命周期内启动一个协程
        val job = lifecycleScope.launchWhenStarted {
            try {
                // 定义协程的作用域
                val scope = this
                // 创建位置回调
                val callbackForRequest = object : ILocationCallback.Stub() {
                    override fun onLocationResult(result: LocationResult?) {
                        // 如果尚未返回结果，则尝试发送位置结果
                        if (!returned) runCatching { callback.onLocationStatus(Status.SUCCESS, result?.lastLocation) }
                        returned = true
                        scope.cancel()
                    }

                    override fun onLocationAvailability(availability: LocationAvailability?) {
                        // 忽略位置可用性更新
                    }

                    override fun cancel() {
                        // 如果尚未返回结果，则发送取消状态
                        if (!returned) runCatching { callback.onLocationStatus(Status.SUCCESS, null) }
                        returned = true
                        scope.cancel()
                    }
                }
                // 构建位置请求配置
                val currentLocationRequest = LocationRequest.Builder(request.priority, 1000)
                    .setGranularity(request.granularity)
                    .setMaxUpdateAgeMillis(request.maxUpdateAgeMillis)
                    .setDurationMillis(request.durationMillis)
                    .setPriority(request.priority)
                    .setWorkSource(request.workSource)
                    .setThrottleBehavior(request.throttleBehavior)
                    .build()
                // 向位置管理器添加请求
                locationManager.addBinderRequest(clientIdentity, binderIdentity, callbackForRequest, currentLocationRequest)
                // 暂停协程，等待取消或结果返回
                awaitCancellation()
            } catch (e: CancellationException) {
                // 如果发生取消异常，不发送结果
            } catch (e: Exception) {
                // 处理其他异常，如果尚未返回结果，则发送错误状态
                try {
                    if (!returned) callback.onLocationStatus(Status(CommonStatusCodes.ERROR, e.message), null)
                    returned = true
                } catch (e2: Exception) {
                    Log.w(TAG, "Failed", e)
                }
            } finally {
                // 无论如何都尝试移除绑定的请求
                runCatching { locationManager.removeBinderRequest(binderIdentity) }
            }
        }
        // 返回一个实现了 ICancelToken 接口的匿名类，允许调用者取消位置请求
        return object : ICancelToken.Stub() {
            override fun cancel() {
                // 如果尚未返回结果，则发送取消状态
                if (!returned) runCatching { callback.onLocationStatus(Status.CANCELED, null) }
                returned = true
                job.cancel()
            }
        }
    }


    // 定义一个函数以获取最后的位置，并通过接收器传递结果
    override fun getLastLocationWithReceiver(request: LastLocationRequest, receiver: LocationReceiver) {
        // 使用日志记录当前通过哪个客户端身份调用该方法
        Log.d(TAG, "getLastLocationWithReceiver by ${getClientIdentity().packageName}")

        // 检查应用是否拥有定位权限
        checkHasAnyLocationPermission()

        // 获取位置状态的回调接口
        val callback = receiver.statusCallback

        // 获取客户端身份信息
        val clientIdentity = getClientIdentity()

        // 在生命周期处于活动状态时启动协程执行位置获取
        lifecycleScope.launchWhenStarted {
            try {
                // 尝试获取最后的位置并回调成功状态
                callback.onLocationStatus(Status.SUCCESS, locationManager.getLastLocation(clientIdentity, request))

            } catch (e: Exception) {
                // 第一次获取位置失败后，尝试回调错误状态
                try {
                    callback.onLocationStatus(Status(CommonStatusCodes.ERROR, e.message), null)
                } catch (e2: Exception) {
                    // 如果回调错误状态也失败，则记录第二次异常
                    Log.w(TAG, "Failed", e)
                }
            }
        }
    }

    // 请求位置设置对话框，用于确认设备的位置服务设置
    override fun requestLocationSettingsDialog(settingsRequest: LocationSettingsRequest?, callback: ISettingsCallbacks?, packageName: String?) {
        // 记录调试日志，包括调用者包名和请求的设置
        Log.d(TAG, "requestLocationSettingsDialog by ${getClientIdentity().packageName} $settingsRequest")
        // 获取调用者身份
        val clientIdentity = getClientIdentity()
        // 在组件的生命周期内启动协程
        lifecycleScope.launchWhenStarted {
            // 获取设备的详细位置设置状态
            val states = context.getDetailedLocationSettingsStates()
            // 将请求的位置服务转换为实际权限和精度要求的对
            val requests = settingsRequest?.requests?.map {
                it.priority to (if (it.granularity == Granularity.GRANULARITY_PERMISSION_LEVEL) context.granularityFromPermission(clientIdentity) else it.granularity)
            }.orEmpty()
            // 判断是否请求了高精度的GPS位置
            val gpsRequested = requests.any { it.first == Priority.PRIORITY_HIGH_ACCURACY && it.second == Granularity.GRANULARITY_FINE }
            // 判断是否请求了网络位置服务
            val networkLocationRequested = requests.any { it.first <= Priority.PRIORITY_LOW_POWER && it.second >= Granularity.GRANULARITY_COARSE }
            // 判断是否请求了蓝牙位置服务
            val bleRequested = settingsRequest?.needBle == true
            // 判断是否需要细粒度的位置权限以扫描WiFi（适用于Android 10及以上版本）
            val networkLocationRequiresFine = context.hasNetworkLocationServiceBuiltIn() && SDK_INT >= 29
            // 根据请求的服务和设备状态计算状态码
            val statusCode = when {
                // 检查权限
                gpsRequested && states.gpsPresent && !states.fineLocationPermission -> CommonStatusCodes.RESOLUTION_REQUIRED
                networkLocationRequested && states.networkLocationPresent && !states.coarseLocationPermission -> CommonStatusCodes.RESOLUTION_REQUIRED
                networkLocationRequested && states.networkLocationPresent && networkLocationRequiresFine && !states.fineLocationPermission -> CommonStatusCodes.RESOLUTION_REQUIRED
                // 检查设备功能是否可用
                gpsRequested && states.gpsPresent && !states.gpsUsable -> CommonStatusCodes.RESOLUTION_REQUIRED
                networkLocationRequested && states.networkLocationPresent && !states.networkLocationUsable -> CommonStatusCodes.RESOLUTION_REQUIRED
                bleRequested && states.blePresent && !states.bleUsable -> CommonStatusCodes.RESOLUTION_REQUIRED
                // 检查设备功能是否存在
                gpsRequested && !states.gpsPresent -> LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE
                networkLocationRequested && !states.networkLocationPresent -> LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE
                bleRequested && !states.blePresent -> LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE
                // 如果所有条件都满足，则成功
                else -> CommonStatusCodes.SUCCESS
            }

            // 如果需要用户解决，创建一个PendingIntent用于解决
            val resolution = if (statusCode == CommonStatusCodes.RESOLUTION_REQUIRED) {
                val intent = Intent(ACTION_LOCATION_SETTINGS_CHECKER)
                intent.setPackage(context.packageName)
                intent.putExtra(EXTRA_ORIGINAL_PACKAGE_NAME, clientIdentity.packageName)
                intent.putExtra(EXTRA_SETTINGS_REQUEST, SafeParcelableSerializer.serializeToBytes(settingsRequest))
                PendingIntentCompat.getActivity(context, clientIdentity.packageName.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT, true)
            } else null
            // 创建一个状态对象，包含状态码和可能的解决方案
            val status = Status(statusCode, LocationSettingsStatusCodes.getStatusCodeString(statusCode), resolution)
            // 记录操作结果
            Log.d(TAG, "requestLocationSettingsDialog by ${getClientIdentity().packageName} returns $status")
            // 尝试执行回调，传递位置设置结果
            runCatching { callback?.onLocationSettingsResult(LocationSettingsResult(status, states.toApi())) }
        }
    }


    // region Mock locations

    override fun setMockModeWithCallback(mockMode: Boolean, callback: IStatusCallback) {
        Log.d(TAG, "setMockModeWithCallback by ${getClientIdentity().packageName}")
        checkHasAnyLocationPermission()
        val clientIdentity = getClientIdentity()
        lifecycleScope.launchWhenStarted {
            try {
                Log.d(TAG, "Not yet implemented: setMockModeWithCallback")
                callback.onResult(Status.SUCCESS)
            } catch (e: Exception) {
                Log.w(TAG, "Failed", e)
            }
        }
    }

    override fun setMockLocationWithCallback(mockLocation: Location, callback: IStatusCallback) {
        Log.d(TAG, "setMockLocationWithCallback by ${getClientIdentity().packageName}")
        checkHasAnyLocationPermission()
        val clientIdentity = getClientIdentity()
        lifecycleScope.launchWhenStarted {
            try {
                Log.d(TAG, "Not yet implemented: setMockLocationWithCallback")
                callback.onResult(Status.SUCCESS)
            } catch (e: Exception) {
                Log.w(TAG, "Failed", e)
            }
        }
    }

    // endregion

    // region Location updates

    override fun registerLocationUpdates(
        oldBinder: IBinder?,
        binder: IBinder,
        callback: ILocationCallback,
        request: LocationRequest,
        statusCallback: IStatusCallback
    ) {
        Log.d(TAG, "registerLocationUpdates (callback) by ${getClientIdentity().packageName}")
        checkHasAnyLocationPermission()
        val clientIdentity = getClientIdentity()
        lifecycleScope.launchWhenStarted {
            try {
                if (oldBinder != null) {
                    locationManager.updateBinderRequest(clientIdentity, oldBinder, binder, callback, request)
                } else {
                    locationManager.addBinderRequest(clientIdentity, binder, callback, request)
                }
                statusCallback.onResult(Status.SUCCESS)
            } catch (e: Exception) {
                try {
                    statusCallback.onResult(Status(CommonStatusCodes.ERROR, e.message))
                } catch (e2: Exception) {
                    Log.w(TAG, "Failed", e)
                }
            }
        }
    }

    override fun registerLocationUpdates(pendingIntent: PendingIntent, request: LocationRequest, statusCallback: IStatusCallback) {
        Log.d(TAG, "registerLocationUpdates (intent) by ${getClientIdentity().packageName}")
        checkHasAnyLocationPermission()
        val clientIdentity = getClientIdentity()
        lifecycleScope.launchWhenStarted {
            try {
                locationManager.addIntentRequest(clientIdentity, pendingIntent, request)
                statusCallback.onResult(Status.SUCCESS)
            } catch (e: Exception) {
                try {
                    statusCallback.onResult(Status(CommonStatusCodes.ERROR, e.message))
                } catch (e2: Exception) {
                    Log.w(TAG, "Failed", e)
                }
            }
        }
    }

    override fun unregisterLocationUpdates(binder: IBinder, statusCallback: IStatusCallback) {
        Log.d(TAG, "unregisterLocationUpdates (callback) by ${getClientIdentity().packageName}")
        lifecycleScope.launchWhenStarted {
            try {
                locationManager.removeBinderRequest(binder)
                statusCallback.onResult(Status.SUCCESS)
            } catch (e: Exception) {
                try {
                    statusCallback.onResult(Status(CommonStatusCodes.ERROR, e.message))
                } catch (e2: Exception) {
                    Log.w(TAG, "Failed", e)
                }
            }
        }
    }

    override fun unregisterLocationUpdates(pendingIntent: PendingIntent, statusCallback: IStatusCallback) {
        Log.d(TAG, "unregisterLocationUpdates (intent) by ${getClientIdentity().packageName}")
        lifecycleScope.launchWhenStarted {
            try {
                locationManager.removeIntentRequest(pendingIntent)
                statusCallback.onResult(Status.SUCCESS)
            } catch (e: Exception) {
                try {
                    statusCallback.onResult(Status(CommonStatusCodes.ERROR, e.message))
                } catch (e2: Exception) {
                    Log.w(TAG, "Failed", e)
                }
            }
        }
    }

    // endregion

    // endregion

    // region Device Orientation

    override fun updateDeviceOrientationRequest(request: DeviceOrientationRequestUpdateData) {
        Log.d(TAG, "updateDeviceOrientationRequest by ${getClientIdentity().packageName}")
        checkHasAnyLocationPermission()
        val clientIdentity = getClientIdentity()
        val callback = request.fusedLocationProviderCallback
        lifecycleScope.launchWhenStarted {
            try {
                when (request.opCode) {
                    REQUEST_UPDATES -> locationManager.deviceOrientationManager.add(clientIdentity, request.request, request.listener)
                    REMOVE_UPDATES -> locationManager.deviceOrientationManager.remove(clientIdentity, request.listener)
                    else -> throw UnsupportedOperationException("Op code ${request.opCode} not supported")
                }
                callback?.onFusedLocationProviderResult(FusedLocationProviderResult.SUCCESS)
            } catch (e: Exception) {
                try {
                    callback?.onFusedLocationProviderResult(FusedLocationProviderResult.create(Status(CommonStatusCodes.ERROR, e.message)))
                } catch (e2: Exception) {
                    Log.w(TAG, "Failed", e)
                }
            }
        }
    }

    // endregion

    private fun getClientIdentity() = ClientIdentity(packageName).apply { uid = getCallingUid(); pid = getCallingPid() }

    private fun checkHasAnyLocationPermission() = checkHasAnyPermission(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION)

    private fun checkHasAnyPermission(vararg permissions: String) {
        for (permission in permissions) {
            if (context.packageManager.checkPermission(permission, packageName) == PERMISSION_GRANTED) {
                return
            }
        }
        throw SecurityException("$packageName does not have any of $permissions")
    }

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean =
        warnOnTransactionIssues(code, reply, flags, TAG) { super.onTransact(code, data, reply, flags) }
}