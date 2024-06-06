package org.microg.gms.fido.core.nativeapp

import android.os.RemoteException
import android.util.Log
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.fido.fido2.api.IBooleanCallback
import com.google.android.gms.fido.fido2.api.common.PublicKeyCredentialCreationOptions
import com.google.android.gms.fido.fido2.api.common.PublicKeyCredentialRequestOptions
import com.google.android.gms.fido.fido2.internal.regular.IFido2AppCallbacks
import com.google.android.gms.fido.fido2.internal.regular.IFido2AppService

class Fido2RegularServiceImpl(private val service: FidoNativeAppApiChimeraService) : IFido2AppService.Stub() {

    @Throws(RemoteException::class)
    override fun createCredential(callbacks: IFido2AppCallbacks, options: PublicKeyCredentialCreationOptions) {
        if (options == null) {
            callbacks.onError(CommonStatusCodes.ERROR, "PublicKeyCredentialCreationOptions is null")
            return
        }
        // 处理创建凭证的逻辑
        Log.d("Fido2RegularServiceImpl", "createCredential")
        callbacks.onSuccess()
    }

    @Throws(RemoteException::class)
    override fun getCredential(callbacks: IFido2AppCallbacks, options: PublicKeyCredentialRequestOptions) {
        if (options == null) {
            callbacks.onError(CommonStatusCodes.ERROR, "PublicKeyCredentialRequestOptions is null")
            return
        }
        // 处理获取凭证的逻辑
        Log.d("Fido2RegularServiceImpl", "getCredential")
        callbacks.onSuccess()
    }

    @Throws(RemoteException::class)
    override fun isUserVerifyingPlatformAuthenticatorAvailable(callback: IBooleanCallback) {
        // 调用平台认证器的接口，判断是否支持用户验证(未实现)

        try {
            // 这里假设平台认证器总是可用的。可以根据实际情况修改此逻辑。
            val isAvailable = true
            callback.onBoolean(isAvailable)
        } catch (e: RemoteException) {
            Log.e("Fido2RegularServiceImpl", "RemoteException in isUserVerifyingPlatformAuthenticatorAvailable", e)
            throw e
        } catch (e: Exception) {
            Log.e("Fido2RegularServiceImpl", "Exception in isUserVerifyingPlatformAuthenticatorAvailable", e)
            throw RemoteException("Exception in isUserVerifyingPlatformAuthenticatorAvailable: ${e.message}")
        }
    }

    @Throws(RemoteException::class)
    override fun isUserVerifyingPlatformAuthenticatorAvailableForCredential(callback: IBooleanCallback, rpId: String, credentialId: ByteArray) {
        if (rpId == null || credentialId == null) {
            callback.onBoolean(false)
            return
        }
        // 处理检查特定凭证的认证器逻辑
        Log.d("Fido2RegularServiceImpl", "isUserVerifyingPlatformAuthenticatorAvailableForCredential")
        callback.onBoolean(true)
    }
}
