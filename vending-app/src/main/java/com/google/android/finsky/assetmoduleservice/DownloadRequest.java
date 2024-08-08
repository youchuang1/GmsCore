package com.google.android.finsky.assetmoduleservice;

import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.HashMap;
import java.util.Map;

public class DownloadRequest extends Request<byte[]> {
    private final VolleyCallback callback;
    private final Context context;
    private static final String USER_AGENT = "Android-Finsky/42.0.20-23 [0] [PR] 654119317 (versionCode=84202000,sdk=33,model=Pixel 4 XL,engine=Cronet;quic=1;http2=1)";
    private final String url;

    public DownloadRequest(Context context, String url, VolleyCallback callback) {
        super(Method.GET, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        });

        this.context = context;
        this.url = url;
        this.callback = callback;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", USER_AGENT);
        headers.put("x-pds-is-network-metered", "0");
        headers.put("accept-encoding", "gzip, deflate");
        headers.put("priority", "u=1, i");
        return headers;
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(byte[] response) {
        callback.onSuccess(response);
    }

    public interface VolleyCallback {
        void onSuccess(byte[] result);
        void onError(String error);
    }
}
