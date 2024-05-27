package com.google.android.gms.fido.fido2.internal.regular;

import com.google.android.gms.fido.fido2.api.common.PublicKeyCredentialCreationOptions;
import com.google.android.gms.fido.fido2.api.common.PublicKeyCredentialRequestOptions;
import com.google.android.gms.fido.fido2.api.IBooleanCallback;
import com.google.android.gms.fido.fido2.internal.regular.IFido2AppCallbacks;

interface IFido2AppService {
    void createCredential(IFido2AppCallbacks callback, in PublicKeyCredentialCreationOptions options);
    void getCredential(IFido2AppCallbacks callback, in PublicKeyCredentialRequestOptions options);
    void isUserVerifyingPlatformAuthenticatorAvailable(IBooleanCallback callback);
    void isUserVerifyingPlatformAuthenticatorAvailableForCredential(IBooleanCallback callback, String rpId, in byte[] credentialId);
}
