package com.google.android.play.core.assetpacks.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
public @interface AssetPackUpdateAvailability {
    public static final int UNKNOWN = 0;
    public static final int UPDATE_AVAILABLE = 2;
    public static final int UPDATE_NOT_AVAILABLE = 1;
}