package com.google.android.play.core.assetpacks.model;

public class StringUtil {
    public static String combine(String key, String moduleName) {
        return key + ":" + moduleName;
    }

    public static String combine(String key, String moduleName, String chunkName) {
        return key + ":" + moduleName + ":" + chunkName;
    }
}
