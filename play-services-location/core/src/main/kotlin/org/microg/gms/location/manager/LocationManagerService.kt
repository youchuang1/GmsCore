/*
 * SPDX-FileCopyrightText: 2023 microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */

package org.microg.gms.location.manager

import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Process
import android.util.Log
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.internal.ConnectionInfo
import com.google.android.gms.common.internal.GetServiceRequest
import com.google.android.gms.common.internal.IGmsCallbacks
import org.microg.gms.BaseService
import org.microg.gms.common.GmsService
import org.microg.gms.common.PackageUtils
import org.microg.gms.location.EXTRA_LOCATION
import org.microg.gms.utils.IntentCacheManager
import java.io.FileDescriptor
import java.io.PrintWriter


class LocationManagerService : BaseService(TAG, GmsService.LOCATION_MANAGER) {
    // 定义一个位置管理器实例
    private val locationManager = LocationManager(this, lifecycle)

    // 服务启动时调用的方法
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 启动位置管理器
        locationManager.start()

        // 如果调用者是同一应用且传入的意图指定了报告位置的动作，则更新网络位置信息
        if (Binder.getCallingUid() == Process.myUid() && intent?.action == ACTION_REPORT_LOCATION) {
            val location = intent.getParcelableExtra<Location>(EXTRA_LOCATION)
            if (location != null) {
                locationManager.updateNetworkLocation(location)
            }
        }

        // 如果意图被缓存，则处理缓存的意图
        if (intent != null && IntentCacheManager.isCache(intent)) {
            locationManager.handleCacheIntent(intent)
        }

        // 调用父类的 onStartCommand 方法
        return super.onStartCommand(intent, flags, startId)
    }

    // 服务销毁时调用的方法
    override fun onDestroy() {
        // 停止位置管理器
        locationManager.stop()
        super.onDestroy()
    }

    // 处理服务请求
    override fun handleServiceRequest(callback: IGmsCallbacks, request: GetServiceRequest, service: GmsService?) {
        // 验证并获取调用者的包名
        val packageName = PackageUtils.getAndCheckCallingPackage(this, request.packageName)
            ?: throw IllegalArgumentException("Missing package name")

        // 启动位置管理器
        locationManager.start()

        // 回调，通知服务初始化完成，并传递连接信息
        callback.onPostInitCompleteWithConnectionInfo(
            CommonStatusCodes.SUCCESS,
            LocationManagerInstance(this, locationManager, packageName, lifecycle).asBinder(),
            ConnectionInfo().apply { features = FEATURES }
        )
    }

    // 提供服务的详细信息，用于调试
    override fun dump(fd: FileDescriptor?, writer: PrintWriter, args: Array<out String>?) {
        super.dump(fd, writer, args)
        locationManager.dump(writer)
    }

    companion object {
        // 定义一个动作常量，用于报告位置
        const val ACTION_REPORT_LOCATION = "org.microg.gms.location.manager.ACTION_REPORT_LOCATION"
    }
}