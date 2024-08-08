package com.google.android.finsky.assetmoduleservice;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.NoCache;

public class NetworkRequestManager {
    private static NetworkRequestManager instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private NetworkRequestManager(Context context) {
        ctx = context.getApplicationContext();
        requestQueue = getRequestQueue();
    }

    public static synchronized NetworkRequestManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkRequestManager(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            NoCache noCache = new NoCache();
            BasicNetwork network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(noCache, network);
            requestQueue.start();
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
