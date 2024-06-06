package org.microg.gms.fido.core.nativeapp

import android.os.IBinder
import android.os.RemoteException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.internal.GetServiceRequest
import com.google.android.gms.common.internal.IGmsCallbacks
import org.microg.gms.BaseService
import org.microg.gms.common.GmsService.FIDO2_REGULAR
import android.util.Log
import org.microg.gms.fido.core.privileged.Fido2PrivilegedServiceImpl

const val TAG = "Fido2NativeAppApiChimera"

class FidoNativeAppApiChimeraService : BaseService(TAG, FIDO2_REGULAR) {
    override fun handleServiceRequest(callbacks: IGmsCallbacks, request: GetServiceRequest) {
        Log.d(TAG, "handleServiceRequest: ${request.serviceId}")

        val binder = Fido2RegularServiceImpl(this).asBinder()

        if (binder == null) {
            callbacks.onPostInitComplete(CommonStatusCodes.ERROR, null, null)
        } else {
            callbacks.onPostInitComplete(CommonStatusCodes.SUCCESS, binder, null)
        }
    }


}
