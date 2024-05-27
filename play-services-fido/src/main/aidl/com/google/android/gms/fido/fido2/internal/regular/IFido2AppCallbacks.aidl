package com.google.android.gms.fido.fido2.internal.regular;

interface IFido2AppCallbacks {
    void onSuccess();
    void onFailure(int errorCode, String errorMessage);
}
