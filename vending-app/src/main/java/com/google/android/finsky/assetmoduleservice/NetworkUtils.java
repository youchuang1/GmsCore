package com.google.android.finsky.assetmoduleservice;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class NetworkUtils {

    public static void sendGetRequest(Context context, String url, final VolleyCallback callback) {
        // 创建请求队列
        RequestQueue queue = Volley.newRequestQueue(context);

        // 创建StringRequest
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 请求成功的处理
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 请求失败的处理
                callback.onError(error.toString());
            }
        });

        // 将请求添加到请求队列
        queue.add(stringRequest);
    }

    // 定义一个接口用于回调请求结果
    public interface VolleyCallback {
        void onSuccess(String result);
        void onError(String error);
    }
}
