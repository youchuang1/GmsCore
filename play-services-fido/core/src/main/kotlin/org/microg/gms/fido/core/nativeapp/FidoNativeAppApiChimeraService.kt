package org.microg.gms.fido.core.api.nativeapp

import com.google.android.gms.common.internal.GetServiceRequest
import org.microg.gms.common.GmsService

class FidoNativeAppApiChimeraService : FidoNativeAppApiService(
    148,
    "com.google.android.gms.fido.fido2.regular.START",
    Permissions.ALL_PERMISSIONS,
    3,
    9
) {
    override fun handleServiceRequest(service: ServiceHandler, request: GetServiceRequest) {
        service.execute(ServiceExecutor(this, request))
    }
}
