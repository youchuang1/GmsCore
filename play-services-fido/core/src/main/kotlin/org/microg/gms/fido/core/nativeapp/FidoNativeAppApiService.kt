package org.microg.gms.fido.core.api.nativeapp

import com.google.android.gms.common.internal.GetServiceRequest

open class FidoNativeAppApiService(
    private val id: Int,
    private val action: String,
    private val permissions: Array<String>,
    private val versionMin: Int,
    private val versionMax: Int
) {
    open fun handleServiceRequest(service: ServiceHandler, request: GetServiceRequest) {
        // 需要在子类中实现具体逻辑
    }
}
