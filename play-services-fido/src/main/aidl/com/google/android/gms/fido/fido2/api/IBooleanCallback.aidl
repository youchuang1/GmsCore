package com.google.android.gms.fido.fido2.api;

import com.google.android.gms.common.api.Status;

interface IBooleanCallback {
    void onResult(boolean result) = 2;
    void onBoolean(boolean value) = 0;
    void onError(in Status status) = 1;
}
