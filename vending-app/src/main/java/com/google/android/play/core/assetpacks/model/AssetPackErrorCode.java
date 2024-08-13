package com.google.android.play.core.assetpacks.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示资源包错误代码的注解。
 * 该注解的值表示资源包操作过程中可能出现的错误类型。
 */
@Retention(RetentionPolicy.CLASS)
public @interface AssetPackErrorCode {

    /**
     * 访问被拒绝。可能是由于权限问题。
     */
    public static final int ACCESS_DENIED = -7;

    /**
     * API 不可用。可能是由于不支持的设备或操作系统版本。
     */
    public static final int API_NOT_AVAILABLE = -5;

    /**
     * 应用未被拥有。用户没有购买或安装该应用。
     */
    public static final int APP_NOT_OWNED = -13;

    /**
     * 应用不可用。可能是由于应用在当前区域不可用。
     */
    public static final int APP_UNAVAILABLE = -1;

    /**
     * 不需要确认。表示操作不需要用户确认。
     */
    public static final int CONFIRMATION_NOT_REQUIRED = -14;

    /**
     * 下载未找到。可能是由于下载信息丢失或被删除。
     */
    public static final int DOWNLOAD_NOT_FOUND = -4;

    /**
     * 存储空间不足。设备上没有足够的存储空间进行操作。
     */
    public static final int INSUFFICIENT_STORAGE = -10;

    /**
     * 内部错误。可能是由于未知的系统或应用内部错误。
     */
    public static final int INTERNAL_ERROR = -100;

    /**
     * 无效请求。可能是由于请求参数不正确。
     */
    public static final int INVALID_REQUEST = -3;

    /**
     * 网络错误。可能是由于网络连接问题。
     */
    public static final int NETWORK_ERROR = -6;

    /**
     * 没有错误。操作成功完成。
     */
    public static final int NO_ERROR = 0;

    /**
     * 资源包不可用。可能是由于资源包在当前区域不可用。
     */
    public static final int PACK_UNAVAILABLE = -2;

    /**
     * 无法识别的安装。可能是由于安装包损坏或被篡改。
     */
    public static final int UNRECOGNIZED_INSTALLATION = -15;
}
