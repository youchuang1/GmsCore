package com.google.android.play.core.assetpacks.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
public @interface AssetPackStatus {
    public static final int CANCELED = 6;
    public static final int COMPLETED = 4;
    public static final int DOWNLOADING = 2;
    public static final int FAILED = 5;
    public static final int NOT_INSTALLED = 8;
    public static final int PENDING = 1;
    public static final int REQUIRES_USER_CONFIRMATION = 9;
    public static final int TRANSFERRING = 3;
    public static final int UNKNOWN = 0;
    public static final int WAITING_FOR_WIFI = 7;
}