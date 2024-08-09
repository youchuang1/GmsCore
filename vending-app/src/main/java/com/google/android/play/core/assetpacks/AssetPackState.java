package com.google.android.play.core.assetpacks;


import com.google.android.play.core.assetpacks.model.AssetPackErrorCode;
import com.google.android.play.core.assetpacks.model.AssetPackStatus;

public abstract class AssetPackState {

    public abstract long bytesDownloaded();

    @AssetPackErrorCode
    public abstract int errorCode();

    public abstract String name();

    @AssetPackStatus
    public abstract int status();

    public abstract long totalBytesToDownload();
}