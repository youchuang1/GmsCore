package com.google.android.finsky.assetmoduleservice;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class DownloadThreadPool {
    private final ExecutorService executorService;

    private static DownloadThreadPool instance;

    private DownloadThreadPool(int poolSize) {
        executorService = Executors.newFixedThreadPool(poolSize);
    }

    public static synchronized DownloadThreadPool getInstance(int poolSize) {
        if (instance == null) {
            instance = new DownloadThreadPool(poolSize);
        }
        return instance;
    }

    public void downloadResources(String packageName, long totalBytes, List<String> urlList) {
        for (String url : urlList) {
            executorService.execute(new DownloadTask(packageName, url));
        }
    }

    public void shutdown() {
        executorService.shutdown();
    }

    private static class DownloadTask implements Runnable {
        private final String packageName;
        private final String fileUrl;

        public DownloadTask(String packageName, String fileUrl) {
            this.packageName = packageName;
            this.fileUrl = fileUrl;
        }

        @Override
        public void run() {
            try {
                downloadFile(packageName, fileUrl);
            } catch (IOException e) {
                System.err.println("下载失败: " + fileUrl + " 错误: " + e.getMessage());
            }
        }

        private void downloadFile(String packageName, String fileUrl) throws IOException {
            URL url = new URL(fileUrl);
            String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            try (BufferedInputStream in = new BufferedInputStream(url.openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(packageName + "_" + fileName)) {
                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                }
                System.out.println("下载完成: " + fileName);
            }
        }
    }
}
