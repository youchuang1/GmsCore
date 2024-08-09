package com.google.android.finsky.assetmoduleservice;

import java.util.Arrays;

public class DownloadInfo {
    private int packVersionDlcHd;
    private int packBaseVersionDlcHd;
    private long totalBytesToDownloadInitPack;
    private int status;
    private long bytesDownloadedInitPack;
    private int statusDlcHd;
    private long totalBytesToDownloadDlcHd;
    private int sessionIdDlcHd;
    private String[] packNames;
    private int errorCodeInitPack;
    private int appVersionCode;
    private long bytesDownloadedDlcHd;
    private int packVersionInitPack;
    private long totalBytesToDownload;
    private int packBaseVersionInitPack;
    private int errorCodeDlcHd;
    private int sessionIdInitPack;
    private int errorCode;
    private int sessionId;
    private int statusInitPack;
    private long bytesDownloaded;

    public DownloadInfo() {
        // 默认构造函数
    }

    // Getter和Setter方法

    public int getPackVersionDlcHd() {
        return packVersionDlcHd;
    }

    public void setPackVersionDlcHd(int packVersionDlcHd) {
        this.packVersionDlcHd = packVersionDlcHd;
    }

    public int getPackBaseVersionDlcHd() {
        return packBaseVersionDlcHd;
    }

    public void setPackBaseVersionDlcHd(int packBaseVersionDlcHd) {
        this.packBaseVersionDlcHd = packBaseVersionDlcHd;
    }

    public long getTotalBytesToDownloadInitPack() {
        return totalBytesToDownloadInitPack;
    }

    public void setTotalBytesToDownloadInitPack(long totalBytesToDownloadInitPack) {
        this.totalBytesToDownloadInitPack = totalBytesToDownloadInitPack;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getBytesDownloadedInitPack() {
        return bytesDownloadedInitPack;
    }

    public void setBytesDownloadedInitPack(long bytesDownloadedInitPack) {
        this.bytesDownloadedInitPack = bytesDownloadedInitPack;
    }

    public int getStatusDlcHd() {
        return statusDlcHd;
    }

    public void setStatusDlcHd(int statusDlcHd) {
        this.statusDlcHd = statusDlcHd;
    }

    public long getTotalBytesToDownloadDlcHd() {
        return totalBytesToDownloadDlcHd;
    }

    public void setTotalBytesToDownloadDlcHd(long totalBytesToDownloadDlcHd) {
        this.totalBytesToDownloadDlcHd = totalBytesToDownloadDlcHd;
    }

    public int getSessionIdDlcHd() {
        return sessionIdDlcHd;
    }

    public void setSessionIdDlcHd(int sessionIdDlcHd) {
        this.sessionIdDlcHd = sessionIdDlcHd;
    }

    public String[] getPackNames() {
        return packNames;
    }

    public void setPackNames(String[] packNames) {
        this.packNames = packNames;
    }

    public int getErrorCodeInitPack() {
        return errorCodeInitPack;
    }

    public void setErrorCodeInitPack(int errorCodeInitPack) {
        this.errorCodeInitPack = errorCodeInitPack;
    }

    public int getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(int appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public long getBytesDownloadedDlcHd() {
        return bytesDownloadedDlcHd;
    }

    public void setBytesDownloadedDlcHd(long bytesDownloadedDlcHd) {
        this.bytesDownloadedDlcHd = bytesDownloadedDlcHd;
    }

    public int getPackVersionInitPack() {
        return packVersionInitPack;
    }

    public void setPackVersionInitPack(int packVersionInitPack) {
        this.packVersionInitPack = packVersionInitPack;
    }

    public long getTotalBytesToDownload() {
        return totalBytesToDownload;
    }

    public void setTotalBytesToDownload(long totalBytesToDownload) {
        this.totalBytesToDownload = totalBytesToDownload;
    }

    public int getPackBaseVersionInitPack() {
        return packBaseVersionInitPack;
    }

    public void setPackBaseVersionInitPack(int packBaseVersionInitPack) {
        this.packBaseVersionInitPack = packBaseVersionInitPack;
    }

    public int getErrorCodeDlcHd() {
        return errorCodeDlcHd;
    }

    public void setErrorCodeDlcHd(int errorCodeDlcHd) {
        this.errorCodeDlcHd = errorCodeDlcHd;
    }

    public int getSessionIdInitPack() {
        return sessionIdInitPack;
    }

    public void setSessionIdInitPack(int sessionIdInitPack) {
        this.sessionIdInitPack = sessionIdInitPack;
    }

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

    public int getStatusInitPack() {
        return statusInitPack;
    }

    public void setStatusInitPack(int statusInitPack) {
        this.statusInitPack = statusInitPack;
    }

    public long getBytesDownloaded() {
        return bytesDownloaded;
    }

    public void setBytesDownloaded(long bytesDownloaded) {
        this.bytesDownloaded = bytesDownloaded;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "packVersionDlcHd=" + packVersionDlcHd +
                ", packBaseVersionDlcHd=" + packBaseVersionDlcHd +
                ", totalBytesToDownloadInitPack=" + totalBytesToDownloadInitPack +
                ", status=" + status +
                ", bytesDownloadedInitPack=" + bytesDownloadedInitPack +
                ", statusDlcHd=" + statusDlcHd +
                ", totalBytesToDownloadDlcHd=" + totalBytesToDownloadDlcHd +
                ", sessionIdDlcHd=" + sessionIdDlcHd +
                ", packNames=" + Arrays.toString(packNames) +
                ", errorCodeInitPack=" + errorCodeInitPack +
                ", appVersionCode=" + appVersionCode +
                ", bytesDownloadedDlcHd=" + bytesDownloadedDlcHd +
                ", packVersionInitPack=" + packVersionInitPack +
                ", totalBytesToDownload=" + totalBytesToDownload +
                ", packBaseVersionInitPack=" + packBaseVersionInitPack +
                ", errorCodeDlcHd=" + errorCodeDlcHd +
                ", sessionIdInitPack=" + sessionIdInitPack +
                ", errorCode=" + errorCode +
                ", sessionId=" + sessionId +
                ", statusInitPack=" + statusInitPack +
                ", bytesDownloaded=" + bytesDownloaded +
                '}';
    }

    public static void main(String[] args) {
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setPackVersionDlcHd(940031);
        downloadInfo.setPackBaseVersionDlcHd(0);
        downloadInfo.setTotalBytesToDownloadInitPack(720672983L);
        downloadInfo.setStatus(8);
        downloadInfo.setBytesDownloadedInitPack(0L);
        downloadInfo.setStatusDlcHd(8);
        downloadInfo.setTotalBytesToDownloadDlcHd(519569342L);
        downloadInfo.setSessionIdDlcHd(8);
        downloadInfo.setPackNames(new String[]{"dlc_hd", "init_pack"});
        downloadInfo.setErrorCodeInitPack(0);
        downloadInfo.setAppVersionCode(940031);
        downloadInfo.setBytesDownloadedDlcHd(0L);
        downloadInfo.setPackVersionInitPack(940031);
        downloadInfo.setTotalBytesToDownload(1240242325L);
        downloadInfo.setPackBaseVersionInitPack(0);
        downloadInfo.setErrorCodeDlcHd(0);
        downloadInfo.setSessionIdInitPack(8);
        downloadInfo.setErrorCode(0);
        downloadInfo.setSessionId(2);
        downloadInfo.setStatusInitPack(8);
        downloadInfo.setBytesDownloaded(0L);

        System.out.println(downloadInfo);
    }
}
