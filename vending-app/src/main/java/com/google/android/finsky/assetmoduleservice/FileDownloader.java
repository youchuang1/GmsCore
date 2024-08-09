package com.google.android.finsky.assetmoduleservice;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader {

    public interface ProgressListener {
        void onProgress(long totalBytes, long downloadedBytes);
        void onError(Exception e);
    }

    private final Context context;

    public FileDownloader(Context context) {
        this.context = context;
    }

    public void downloadFile(String url, final File destination, final ProgressListener listener) {
        new DownloadTask(url, destination, listener).execute();
    }

    private static class DownloadTask extends AsyncTask<Void, Long, Exception> {
        private final String url;
        private final File destination;
        private final ProgressListener listener;

        DownloadTask(String url, File destination, ProgressListener listener) {
            this.url = url;
            this.destination = destination;
            this.listener = listener;
        }

        @Override
        protected Exception doInBackground(Void... voids) {
            HttpURLConnection connection = null;
            BufferedOutputStream bos = null;
            try {
                URL downloadUrl = new URL(url);
                connection = (HttpURLConnection) downloadUrl.openConnection();
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    return new IOException("Server returned HTTP " + responseCode + " " + connection.getResponseMessage());
                }

                long totalBytes = connection.getContentLength();
                try (InputStream input = new BufferedInputStream(connection.getInputStream())) {

                    // 确保目标目录存在
                    File parentDir = destination.getParentFile();
                    if (parentDir != null && !parentDir.exists()) {
                        if (!parentDir.mkdirs()) {
                            return new IOException("Failed to create directories: " + parentDir.getAbsolutePath());
                        }
                    }

                    bos = new BufferedOutputStream(new FileOutputStream(destination));
                    byte[] buffer = new byte[4096];
                    long downloadedBytes = 0;
                    int count;
                    while ((count = input.read(buffer)) != -1) {
                        bos.write(buffer, 0, count);
                        downloadedBytes += count;
                        publishProgress(totalBytes, downloadedBytes);
                    }
                }
            } catch (Exception e) {
                return e;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        Log.e("FileDownloader", "Error closing BufferedOutputStream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            if (listener != null) {
                listener.onProgress(values[0], values[1]);
            }
        }

        @Override
        protected void onPostExecute(Exception e) {
            if (listener != null && e != null) {
                listener.onError(e);
            }
        }
    }
}