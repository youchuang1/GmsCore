package com.google.android.finsky.assetmoduleservice;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDownloader {

    private final RequestQueue requestQueue;

    public interface ProgressListener {
        void onProgress(long totalBytes, long downloadedBytes);
    }

    public FileDownloader(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void downloadFile(String url, final File destination, final ProgressListener listener) {
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        long totalBytes = response.length;
                        long downloadedBytes = 0;

                        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destination))) {
                            bos.write(response);
                            downloadedBytes = totalBytes;
                            if (listener != null) {
                                listener.onProgress(totalBytes, downloadedBytes);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    public class InputStreamVolleyRequest extends Request<byte[]> {
        private final Response.Listener<byte[]> listener;

        public InputStreamVolleyRequest(int method, String url, Response.Listener<byte[]> listener, Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            this.listener = listener;
        }

        @Override
        protected Response<byte[]> parseNetworkResponse(com.android.volley.NetworkResponse response) {
            return Response.success(response.data, com.android.volley.toolbox.HttpHeaderParser.parseCacheHeaders(response));
        }

        @Override
        protected void deliverResponse(byte[] response) {
            listener.onResponse(response);
        }
    }
}