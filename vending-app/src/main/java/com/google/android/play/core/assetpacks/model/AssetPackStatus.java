package com.google.android.play.core.assetpacks.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示资源包状态的注解。
 */
@Retention(RetentionPolicy.CLASS)
public @interface AssetPackStatus {
    /** 资源包下载已取消 */
    public static final int CANCELED = 6;
    /** 资源包下载已完成 */
    public static final int COMPLETED = 4;
    /** 资源包正在下载 */
    public static final int DOWNLOADING = 2;
    /** 资源包下载失败 */
    public static final int FAILED = 5;
    /** 资源包未安装 */
    public static final int NOT_INSTALLED = 8;
    /** 资源包等待下载 */
    public static final int PENDING = 1;
    /** 资源包需要用户确认 */
    public static final int REQUIRES_USER_CONFIRMATION = 9;
    /** 资源包正在传输 */
    public static final int TRANSFERRING = 3;
    /** 资源包状态未知 */
    public static final int UNKNOWN = 0;
    /** 资源包等待WiFi连接 */
    public static final int WAITING_FOR_WIFI = 7;
}
