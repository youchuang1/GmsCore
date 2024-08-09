package com.google.android.finsky.assetmoduleservice;

import com.google.android.play.core.assetpacks.AssetPackState;

public class AssetPackStateImp extends AssetPackState {
    @Override
    public long bytesDownloaded() {
        return 0;
    }

    @Override
    public int errorCode() {
        return 0;
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public int status() {
        return 0;
    }

    @Override
    public long totalBytesToDownload() {
        return 0;
    }
}
