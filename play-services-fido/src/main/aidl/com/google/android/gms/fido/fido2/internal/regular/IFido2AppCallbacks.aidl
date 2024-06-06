package com.google.android.gms.fido.fido2.internal.regular;

interface IFido2AppCallbacks {
    void onSuccess();
    void onError(int errorCode, String errorMessage); // 添加 onError 方法
}
