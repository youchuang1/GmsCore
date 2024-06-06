package org.microg.gms.fido.core.nativeapp

import com.google.android.gms.common.Feature
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.internal.GetServiceRequest
import com.google.android.gms.common.internal.IGmsCallbacks
import org.microg.gms.BaseService
import org.microg.gms.common.GmsService
import org.microg.gms.common.GmsService.FIDO2_REGULAR
import android.util.Log
import com.google.android.gms.common.internal.ConnectionInfo

const val TAG = "Fido2NativeAppApiChimeraService"

val FEATURES = arrayOf(
        Feature("is_user_verifying_platform_authenticator_available", 1)
)

class FidoNativeAppApiChimeraService : BaseService(TAG, FIDO2_REGULAR) {
    override fun handleServiceRequest(callback: IGmsCallbacks, request: GetServiceRequest, service: GmsService) {
        // 检查请求是否包含所需的信息
        if (!isValidRequest(request)) {
            Log.e(TAG, "Invalid service request")
            callback.onPostInitComplete(CommonStatusCodes.ERROR, null, null)
            return
        }

        // 初始化服务实现
        val binder = Fido2RegularServiceImpl(this).asBinder()

        // 设置连接信息和特性
        val connectionInfo = ConnectionInfo().apply {
            features = FEATURES
        }

        // 回调通知服务初始化完成
        callback.onPostInitCompleteWithConnectionInfo(
                CommonStatusCodes.SUCCESS, binder, connectionInfo
        )
    }

    // 检查请求是否有效
    private fun isValidRequest(request: GetServiceRequest): Boolean {
        // 在这里添加对请求的验证逻辑（可选）
        // 例如：检查请求是否包含必要的权限或参数
        return request.packageName != null && request.packageName.isNotEmpty()
    }
}
