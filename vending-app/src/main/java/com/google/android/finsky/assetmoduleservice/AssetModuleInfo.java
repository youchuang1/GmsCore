package com.google.android.finsky.assetmoduleservice;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.util.Log;

import com.android.vending.AssetModuleDeliveryRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.Map;

class AssetModuleInfo {
    private PackageManager packageManager;
    private Object assetModule;
    private Object authHandler;
    private Object ownershipChecker;
    private RequestQueue requestQueue;

    public AssetModuleInfo(Context context, PackageManager packageManager, Object assetModule, Object authHandler, Object ownershipChecker) {
        this.packageManager = packageManager;
        this.assetModule = assetModule;
        this.authHandler = authHandler;
        this.ownershipChecker = ownershipChecker;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public final int checkPackagePermissions(String packageName, int someValue) {
        if (packageName != null) {
            int callingUid = Binder.getCallingUid();
            String[] callerPackages = packageManager.getPackagesForUid(callingUid);
            if (callerPackages != null && containsPackageName(callerPackages, packageName)) {
                if (!isPermittedByGlobalFlag(packageName, ((AssetModuleController) this.assetModule).getConfig("AssetModules", "asset_module_package_controller"))) {
                    return -5;
                }
                if (!((AuthenticationHandler) this.authHandler).isAuthenticated()) {
                    return -5;
                }
                if (!((OwnershipChecker) this.ownershipChecker).isOwned(packageName) && !hasSpecialPermission()) {
                    return isSpecialConditionMet(((AssetModuleController) this.assetModule), someValue) ? -13 : -5;
                }
                return 0;
            }
        }
        return -5;
    }

    public final void sendPostRequest(String url, final AssetModuleDeliveryRequest requestPayload, final Map<String, String> headers, final VolleyCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Response is: " + response);
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "That didn't work!", error);
                callback.onError(error.toString());
            }
        }) {
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
        };

        // 将请求添加到请求队列
        requestQueue.add(stringRequest);
    }

    // 定义一个接口用于回调请求结果
    public interface VolleyCallback {
        void onSuccess(String result);
        void onError(String error);
    }

    private static boolean containsPackageName(String[] packageNames, String targetPackage) {
        for (String packageName : packageNames) {
            if (packageName.equals(targetPackage)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPermittedByGlobalFlag(String packageName, String globalFlag) {
        return true;
    }

    private static boolean hasSpecialPermission() {
        return true;
    }

    private static boolean isSpecialConditionMet(AssetModuleController assetModuleController, int value) {
        return true;
    }

    private static void logError(String message, Object... args) {
    }

    private static final String TAG = "AssetModuleInfo";
}

class PackageManagerHelper {
    public PackageManager d;
}

class AssetModuleController {
    public String getConfig(String module, String controller) {
        return "someConfig";
    }
}

class AuthenticationHandler {
    public boolean isAuthenticated() {
        return true;
    }
}

class OwnershipChecker {
    public boolean isOwned(String packageName) {
        return true;
    }
}
