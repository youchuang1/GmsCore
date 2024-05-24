package org.microg.gms.fido.core.api.nativeapp

import com.google.android.gms.common.internal.GetServiceRequest

class ServiceExecutor(
    private val service: FidoNativeAppApiChimeraService,
    private val request: GetServiceRequest
) {
    // 执行相关逻辑
}
