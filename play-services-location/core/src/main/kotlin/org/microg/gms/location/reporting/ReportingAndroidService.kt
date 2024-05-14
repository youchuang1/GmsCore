/*
 * SPDX-FileCopyrightText: 2023 microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */
package org.microg.gms.location.reporting

import android.os.RemoteException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.internal.ConnectionInfo
import com.google.android.gms.common.internal.GetServiceRequest
import com.google.android.gms.common.internal.IGmsCallbacks
import org.microg.gms.BaseService
import org.microg.gms.common.GmsService
import org.microg.gms.common.PackageUtils
import org.microg.gms.location.manager.FEATURES

class ReportingAndroidService : BaseService("GmsLocReportingSvc", GmsService.LOCATION_REPORTING) {
    // 继承自BaseService，初始化时设置服务名称和类型

    @Throws(RemoteException::class)
    // 表明该方法可能抛出远程异常
    override fun handleServiceRequest(callback: IGmsCallbacks, request: GetServiceRequest, service: GmsService) {
        // 处理服务请求的方法实现
        val packageName = PackageUtils.getAndCheckCallingPackage(this, request.packageName)
        // 获取并校验调用者的包名，如果获取失败则抛出异常
            ?: throw IllegalArgumentException("Missing package name")

        // 使用callback回调，通知请求者服务初始化完成
        callback.onPostInitCompleteWithConnectionInfo(
            CommonStatusCodes.SUCCESS,  // 返回状态码，表示成功
            ReportingServiceInstance(this, packageName),  // 创建服务实例
            ConnectionInfo().apply { features = FEATURES }  // 设置连接信息，其中包括服务特性
        )
    }
}

