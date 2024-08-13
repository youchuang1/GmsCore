package com.google.android.finsky.assetmoduleservice;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AppData {
    // 全局字段
    private String packageName;
    private int errorCode;
    private int sessionId;
    private long bytesDownloaded;
    private int status;
    private ArrayList<String> packNames;
    private int appVersionCode;
    private long totalBytesToDownload;

    // 包数据
    private Map<String, PackData> packData;

    public AppData() {
        this.packData = new HashMap<>();
    }


    // 内部类，用于存储每个包的数据
    public static class PackData {
        private int packVersion;
        private int packBaseVersion;
        private int sessionId;
        private int errorCode;
        private int status;
        private long bytesDownloaded;
        private long totalBytesToDownload;
        private String packVersionTag;
        private ArrayList<Bundle> bundleList;




        public ArrayList<Bundle> getBundleList() {
            return bundleList;
        }

        public void setBundleList(ArrayList<Bundle> bundleList) {
            this.bundleList = bundleList;
        }
        // getters and setters
        public int getPackVersion() {
            return packVersion;
        }

        public void setPackVersion(int packVersion) {
            this.packVersion = packVersion;
        }

        public int getPackBaseVersion() {
            return packBaseVersion;
        }

        public void setPackBaseVersion(int packBaseVersion) {
            this.packBaseVersion = packBaseVersion;
        }

        public int getSessionId() {
            return sessionId;
        }

        public void setSessionId(int sessionId) {
            this.sessionId = sessionId;
        }

        public int getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(int errorCode) {
            this.errorCode = errorCode;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public long getBytesDownloaded() {
            return bytesDownloaded;
        }

        public void setBytesDownloaded(long bytesDownloaded) {
            this.bytesDownloaded = bytesDownloaded;
        }

        public int getTotalBytesToDownload() {
            return (int) totalBytesToDownload;
        }

        public void setTotalBytesToDownload(long totalBytesToDownload) {
            this.totalBytesToDownload = totalBytesToDownload;
        }

        public String getPackVersionTag() {
            return packVersionTag;
        }

        public void setPackVersionTag(String packVersionTag) {
            this.packVersionTag = packVersionTag;
        }
    }

    // getters and setters for global fields
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }


    public long getBytesDownloaded() {
        return bytesDownloaded;
    }

    public void setBytesDownloaded(long bytesDownloaded) {
        this.bytesDownloaded = bytesDownloaded;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<String> getPackNames() {
        return packNames;
    }

    public void setPackNames(ArrayList<String> packNames) {
        this.packNames = packNames;
    }






    public int getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(int appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public long getTotalBytesToDownload() {
        return totalBytesToDownload;
    }

    public void setTotalBytesToDownload(long totalBytesToDownload) {
        this.totalBytesToDownload = totalBytesToDownload;
    }

    public Map<String, PackData> getPackData() {
        return packData;
    }

    public void setPackData(Map<String, PackData> packData) {
        this.packData = packData;
    }


    public void addPackData(String packName, PackData data) {
        this.packData.put(packName, data);
    }

    // 删除包数据
    public void removePackData(String packName) {
        this.packData.remove(packName);
    }

    // 获取包数据
    public PackData getPackData(String packName) {
        return this.packData.get(packName);
    }

    // 更新包数据
    public void updatePackData(String packName, PackData data) {
        this.packData.put(packName, data);
    }

    // 增加或更新包字段
    public void setPackField(String packName, String field, Object value) {
        PackData data = this.packData.get(packName);
        if (data != null) {
            switch (field) {
                case "packVersion":
                    data.setPackVersion((Integer) value);
                    break;
                case "packBaseVersion":
                    data.setPackBaseVersion((Integer) value);
                    break;
                case "sessionId":
                    data.setSessionId((Integer) value);
                    break;
                case "errorCode":
                    data.setErrorCode((Integer) value);
                    break;
                case "status":
                    data.setStatus((Integer) value);
                    break;
                case "bytesDownloaded":
                    data.setBytesDownloaded((Long) value);
                    break;
                case "totalBytesToDownload":
                    data.setTotalBytesToDownload((Long) value);
                    break;
                case "packVersionTag":
                    data.setPackVersionTag((String) value);
                    break;
            }
        }
    }

    // 获取包字段
    public Object getPackField(String packName, String field) {
        PackData data = this.packData.get(packName);
        if (data != null) {
            switch (field) {
                case "packVersion":
                    return data.getPackVersion();
                case "packBaseVersion":
                    return data.getPackBaseVersion();
                case "sessionId":
                    return data.getSessionId();
                case "errorCode":
                    return data.getErrorCode();
                case "status":
                    return data.getStatus();
                case "bytesDownloaded":
                    return data.getBytesDownloaded();
                case "totalBytesToDownload":
                    return data.getTotalBytesToDownload();
                case "packVersionTag":
                    return data.getPackVersionTag();
            }
        }
        return null;
    }

    // 增加包的已下载字节数
    public void incrementPackBytesDownloaded(String packName, long bytes) {
        PackData data = this.packData.get(packName);
        if (data != null) {
            data.setBytesDownloaded(data.getBytesDownloaded() + bytes);
        }
    }
    // 增加全局已下载字节数
    public void incrementBytesDownloaded(long bytes) {
        this.bytesDownloaded += bytes;
    }
}