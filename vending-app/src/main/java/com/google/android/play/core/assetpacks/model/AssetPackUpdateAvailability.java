package com.google.android.play.core.assetpacks.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示资源包更新可用性的注解。
 * 该注解的值表示资源包更新的状态。
 */
@Retention(RetentionPolicy.CLASS)
public @interface AssetPackUpdateAvailability {

    /**
     * 状态未知。可能是由于无法获取更新信息。
     */
    public static final int UNKNOWN = 0;

    /**
     * 有更新可用。表示存在可用的资源包更新。
     */
    public static final int UPDATE_AVAILABLE = 2;

    /**
     * 没有更新可用。表示当前资源包已经是最新版本。
     */
    public static final int UPDATE_NOT_AVAILABLE = 1;
}
