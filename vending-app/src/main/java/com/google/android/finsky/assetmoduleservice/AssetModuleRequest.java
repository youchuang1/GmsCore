package com.google.android.finsky.assetmoduleservice;

import com.android.vending.AssetModuleDeliveryRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.IOException;
import java.util.Map;

public final class AssetModuleRequest extends Request<byte[]> {
    private final AssetModuleDeliveryRequest requestPayload;
    private final Map<String, String> headers;
    private final VolleyCallback callback;

    public AssetModuleRequest(String url, AssetModuleDeliveryRequest requestPayload, Map<String, String> headers, VolleyCallback callback) {
        super(Method.POST, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        });
        this.requestPayload = requestPayload;
        this.headers = headers;
        this.callback = callback;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return AssetModuleDeliveryRequest.ADAPTER.encode(requestPayload);
    }

    @Override
    public String getBodyContentType() {
        return "application/x-protobuf";
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(byte[] response) {
        callback.onSuccess(response);
    }

    // 定义一个接口用于回调请求结果
    public interface VolleyCallback {
        void onSuccess(byte[] result);
        void onError(String error);
    }
}