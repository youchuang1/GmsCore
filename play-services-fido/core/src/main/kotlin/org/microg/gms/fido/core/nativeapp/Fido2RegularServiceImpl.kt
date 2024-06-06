package org.microg.gms.fido.core.nativeapp

import android.os.RemoteException
import com.google.android.gms.fido.fido2.api.IBooleanCallback
import com.google.android.gms.fido.fido2.api.common.PublicKeyCredentialCreationOptions
import com.google.android.gms.fido.fido2.api.common.PublicKeyCredentialRequestOptions
import com.google.android.gms.fido.fido2.internal.regular.IFido2AppCallbacks
import com.google.android.gms.fido.fido2.internal.regular.IFido2AppService

class Fido2RegularServiceImpl(private val service: FidoNativeAppApiChimeraService) : IFido2AppService.Stub() {

    @Throws(RemoteException::class)
    override fun createCredential(callbacks: IFido2AppCallbacks, options: PublicKeyCredentialCreationOptions) {
        // 处理创建凭证的逻辑
        callbacks.onSuccess()
    }

    @Throws(RemoteException::class)
    override fun getCredential(callbacks: IFido2AppCallbacks, options: PublicKeyCredentialRequestOptions) {
        // 处理获取凭证的逻辑
        callbacks.onSuccess()
    }

    @Throws(RemoteException::class)
    override fun isUserVerifyingPlatformAuthenticatorAvailable(callback: IBooleanCallback) {
        // 处理检查平台认证器的逻辑
        callback.onResult(true)
    }

    @Throws(RemoteException::class)
    override fun isUserVerifyingPlatformAuthenticatorAvailableForCredential(callback: IBooleanCallback, rpId: String, credentialId: ByteArray) {
        // 处理检查特定凭证的认证器逻辑
        callback.onResult(true)
    }
}
