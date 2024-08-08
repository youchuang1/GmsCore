package com.google.android.finsky.assetmoduleservice;

import android.accounts.Account;
import android.accounts.AccountManager;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import android.util.Log;
import android.widget.Toast;


import androidx.webkit.internal.ApiFeature;

import com.android.vending.AssetModuleDeliveryRequest;
import com.android.vending.Bbvz;
import com.android.vending.Bcmf;
import com.android.vending.Bdpo;
import com.android.vending.Bdpp;
import com.android.vending.IntermediateIntegrityResponseWrapperExtend;


import com.google.android.play.core.assetpacks.protocol.IAssetModuleService;
import com.google.android.play.core.assetpacks.protocol.IAssetModuleServiceCallback;


import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetModuleService extends Service {
    private static final String TAG = "AssetModuleService";
    private AssetModuleInfo assetModuleInfo;
    public Context context;
    private Account user;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        assetModuleInfo = new AssetModuleInfo(this, getPackageManager(), new AssetModuleController(), new AuthenticationHandler(), new OwnershipChecker());
        user = getAccount();
    }

    private Account getAccount() {
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        if (accounts.length > 0) {
            return accounts[0];
        }
        return null;
    }

    private String getAppVersionCode(String packageName) {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            return String.valueOf(packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }


    private final IAssetModuleService.Stub service = new IAssetModuleService.Stub() {

        @Override
        public void startDownload(String packageName, List<Bundle> list, Bundle bundle, IAssetModuleServiceCallback callback) throws RemoteException {
            Log.d(TAG, "Method (startDownload) called by packageName -> " + packageName);
            int playcore_version_code = assetModuleInfo.checkPackagePermissions(packageName, bundle.getInt("playcore_version_code", 0));
            if (playcore_version_code != 0) {
                Bundle bundle1 = new Bundle();
                bundle1.putInt("error_code", playcore_version_code);
                callback.onError(bundle1);
            } else {
                Log.d(TAG, packageName + "----" + playcore_version_code + "----");
                Bundle result = new Bundle();
                result.putStringArrayList("pack_names", new ArrayList<>());
                callback.onStartDownload(-1, result);
            }
        }

        @Override
        public void getSessionStates(String packageName, Bundle bundle, IAssetModuleServiceCallback callback) throws RemoteException {
            int playcore_version_code = assetModuleInfo.checkPackagePermissions(packageName, bundle.getInt("playcore_version_code", 0));
            if (playcore_version_code != 0) {
                Bundle bundle1 = new Bundle();
                bundle1.putInt("error_code", playcore_version_code);
                callback.onError(bundle1);
            } else {
                Log.d(TAG, "Method (getSessionStates) called by packageName -> " + packageName);

            }
        }


        @Override
        public void notifyChunkTransferred(String packageName, Bundle bundle, Bundle bundle2, IAssetModuleServiceCallback callback) throws RemoteException {
            Log.d(TAG, "Method (notifyChunkTransferred) called but not implemented by packageName -> " + packageName);
            int playcore_version_code = assetModuleInfo.checkPackagePermissions(packageName, bundle.getInt("playcore_version_code", 0));
            if (playcore_version_code != 0) {
                Bundle bundle1 = new Bundle();
                bundle1.putInt("error_code", playcore_version_code);
                callback.onError(bundle1);
            } else {
                Log.d(TAG, packageName + "----" + playcore_version_code + "----");
            }
        }

        @Override
        public void notifyModuleCompleted(String packageName, Bundle bundle, Bundle bundle2, IAssetModuleServiceCallback callback) throws RemoteException {
            Log.d(TAG, "Method (notifyModuleCompleted) called but not implemented by packageName -> " + packageName);
            int playcore_version_code = assetModuleInfo.checkPackagePermissions(packageName, bundle.getInt("playcore_version_code", 0));
            if (playcore_version_code != 0) {
                Bundle bundle1 = new Bundle();
                bundle1.putInt("error_code", playcore_version_code);
                callback.onError(bundle1);
            } else {
                Log.d(TAG, packageName + "----" + playcore_version_code + "----");
                // 正常处理逻辑
            }
        }

        @Override
        public void notifySessionFailed(String packageName, Bundle bundle, Bundle bundle2, IAssetModuleServiceCallback callback) throws RemoteException {
            int playcore_version_code = assetModuleInfo.checkPackagePermissions(packageName, bundle.getInt("playcore_version_code", 0));
            if (playcore_version_code != 0) {
                Bundle bundle1 = new Bundle();
                bundle1.putInt("error_code", playcore_version_code);
                callback.onError(bundle1);
            } else {
                Log.d(TAG, "Method (notifySessionFailed) called but not implemented by packageName -> " + packageName);
            }
        }

        @Override
        public void keepAlive(String packageName, Bundle bundle, IAssetModuleServiceCallback callback) throws RemoteException {
            int playcore_version_code = assetModuleInfo.checkPackagePermissions(packageName, bundle.getInt("playcore_version_code", 0));
            if (playcore_version_code != 0) {
                Bundle bundle1 = new Bundle();
                bundle1.putInt("error_code", playcore_version_code);
                callback.onError(bundle1);
            } else {
                Log.d(TAG, "Method (keepAlive) called but not implemented by packageName -> " + packageName);
            }
        }

        @Override
        public void getChunkFileDescriptor(String packageName, Bundle bundle, Bundle bundle2, IAssetModuleServiceCallback callback) throws RemoteException {
            Log.d(TAG, "Method (getChunkFileDescriptor) called but not implemented by packageName -> " + packageName);
            int playcore_version_code = assetModuleInfo.checkPackagePermissions(packageName, bundle.getInt("playcore_version_code", 0));
            if (playcore_version_code != 0) {
                Bundle bundle1 = new Bundle();
                bundle1.putInt("error_code", playcore_version_code);
                callback.onError(bundle1);
            } else {
                Log.d(TAG, packageName + "----" + playcore_version_code + "----");
                // 正常处理逻辑
            }
        }

        @Override
        public void requestDownloadInfo(String packageName, List<Bundle> list, Bundle bundle, IAssetModuleServiceCallback callback) throws RemoteException {
            Log.d(TAG, "Method (requestDownloadInfo) called by packageName -> " + packageName);
            int playcore_version_code = assetModuleInfo.checkPackagePermissions(packageName, bundle.getInt("playcore_version_code", 0));
            if (playcore_version_code != 0) {
                Bundle errorBundle = new Bundle();
                errorBundle.putInt("error_code", playcore_version_code);
                callback.onError(errorBundle);
            } else {
                Log.d(TAG, packageName + "----" + playcore_version_code + "----");
                String versionCode = getAppVersionCode(packageName);

                if (versionCode != null) {
                    Log.d(TAG,versionCode);
                }

                // 构建请求负载的构建器
                AssetModuleDeliveryRequest.Builder requestBuilder = new AssetModuleDeliveryRequest.Builder()
                        .pkgname(packageName)
                        .c(new Bbvz.Builder()
                                .oneofField1(Integer.valueOf(versionCode))
                                .d(3)
                                .build())
                        .playCoreVersion(bundle.getInt("playcore_version_code"))
                        .supportedCompressionFormats(Arrays.asList(Bdpo.UNKNOWN_SEARCH_TRAFFIC_SOURCE, Bdpo.BOOKS_HOME_PAGE))
                        .supportedPatchFormats(Arrays.asList(Bdpp.CALLER_APP_REQUEST, Bdpp.CALLER_APP_DEBUGGABLE))
                        .isInstantApp(false);

                // 循环处理list，获取每个bundle的module_name值，并添加到请求模块列表中
                for (Bundle b : list) {
                    String moduleName = b.getString("module_name");
                    if (moduleName != null) {
                        requestBuilder.requestedAssetModules(Arrays.asList(new Bcmf.Builder().b(moduleName).build()));
                    }
                }

                // 构建最终的请求负载
                AssetModuleDeliveryRequest requestPayload = requestBuilder.build();

                // 打印请求负载以调试
                Log.d(TAG, String.valueOf(requestPayload));


                AssetModuleRequest request = new AssetModuleRequest(context,"https://play-fe.googleapis.com/fdfe/assetModuleDelivery",
                        requestPayload,user, new AssetModuleRequest.VolleyCallback() {
                    @Override
                    public void onSuccess(byte[] result) {
                        try {
                            IntermediateIntegrityResponseWrapperExtend response = IntermediateIntegrityResponseWrapperExtend.ADAPTER.decode(result);
                            Log.d("Response", response.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("Error", error);
                    }
                });

                MySingleton.getInstance(context).addToRequestQueue(request);
            }
        }

        @Override
        public void removeModule(String packageName, Bundle bundle, Bundle bundle2, IAssetModuleServiceCallback callback) throws RemoteException {
            int playcore_version_code = assetModuleInfo.checkPackagePermissions(packageName, bundle.getInt("playcore_version_code", 0));
            if (playcore_version_code != 0) {
                Bundle bundle1 = new Bundle();
                bundle1.putInt("error_code", playcore_version_code);
                callback.onError(bundle1);
            } else {
                Log.d(TAG, "Method (removeModule) called but not implemented by packageName -> " + packageName);
            }
        }

        @Override
        public void cancelDownloads(String packageName, List<Bundle> list, Bundle bundle, IAssetModuleServiceCallback callback) throws RemoteException {
            int playcore_version_code = assetModuleInfo.checkPackagePermissions(packageName, bundle.getInt("playcore_version_code", 0));
            if (playcore_version_code != 0) {
                Bundle bundle1 = new Bundle();
                bundle1.putInt("error_code", playcore_version_code);
                callback.onError(bundle1);
            } else {
                Log.d(TAG, "Method (cancelDownloads) called but not implemented by packageName -> " + packageName);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return service.asBinder();
    }
}
