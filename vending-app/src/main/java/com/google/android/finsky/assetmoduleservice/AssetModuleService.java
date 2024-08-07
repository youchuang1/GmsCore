package com.google.android.finsky.assetmoduleservice;

import android.accounts.Account;
import android.accounts.AccountManager;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import android.util.Log;


import com.android.vending.AssetModuleDeliveryRequest;
import com.android.vending.Bbvz;
import com.android.vending.Bcmf;
import com.android.vending.Bdpo;
import com.android.vending.Bdpp;
import com.android.vending.IntermediateIntegrityResponseWrapperExtend;


import com.google.android.play.core.assetpacks.protocol.IAssetModuleService;
import com.google.android.play.core.assetpacks.protocol.IAssetModuleServiceCallback;


import java.io.IOException;
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
        // 初始化 AssetModuleInfo 实例
        assetModuleInfo = new AssetModuleInfo(this, getPackageManager(), new AssetModuleController(), new AuthenticationHandler(), new OwnershipChecker());
        user = getAccount();
    }

    private Account getAccount() {
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        if (accounts.length > 0) {
            return accounts[0];
        }
        return null; // 处理没有找到账户的情况
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

                AssetModuleDeliveryRequest requestPayload = new AssetModuleDeliveryRequest.Builder()
                        .pkgname("com.gameloft.android.ANMP.GloftDYHM")
                        .c(new Bbvz.Builder()
                                .oneofField1(940031)
                                .d(3)
                                .build())
                        .playCoreVersion(20201)
                        .supportedCompressionFormats(Arrays.asList(Bdpo.UNKNOWN_SEARCH_TRAFFIC_SOURCE, Bdpo.BOOKS_HOME_PAGE))
                        .supportedPatchFormats(Arrays.asList(Bdpp.CALLER_APP_REQUEST, Bdpp.CALLER_APP_DEBUGGABLE))
                        .requestedAssetModules(Arrays.asList(new Bcmf.Builder()
                                .b("dlc_hd")
                                .c(7738135720018340206L)
                                .build()))
                        .isInstantApp(false)
                        .build();



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

                // 将请求添加到请求队列
                MySingleton.getInstance(context).addToRequestQueue(request);
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

                // 创建 bundle1 对象并填充数据
                Bundle bundle1 = new Bundle();
                bundle1.putStringArrayList("pack_names", new ArrayList<>(Arrays.asList("dlc_hd", "init_pack")));
                bundle1.putLong("total_bytes_to_download", 1240242325L);
                bundle1.putInt("app_version_code", 940031);
                bundle1.putInt("session_id", 2);
                bundle1.putInt("status", 8);
                bundle1.putInt("error_code", 0);
                bundle1.putLong("bytes_downloaded", 0L);

                bundle1.putLong("total_bytes_to_download:init_pack", 720672983L);
                bundle1.putLong("total_bytes_to_download:dlc_hd", 519569342L);
                bundle1.putLong("bytes_downloaded:init_pack", 0L);
                bundle1.putLong("bytes_downloaded:dlc_hd", 0L);
                bundle1.putLong("pack_version:init_pack", 940031L);
                bundle1.putLong("pack_version:dlc_hd", 940031L);
                bundle1.putLong("pack_base_version:init_pack", 0L);
                bundle1.putLong("pack_base_version:dlc_hd", 0L);
                bundle1.putLong("session_id:init_pack", 8L);
                bundle1.putLong("session_id:dlc_hd", 8L);

                // 创建 bundle2 对象并填充数据
                Bundle bundle2 = new Bundle();
                bundle2.putStringArrayList("pack_names", new ArrayList<>(Arrays.asList("dlc_hd", "init_pack")));
                bundle2.putLong("total_bytes_to_download", 1240242325L);
                bundle2.putInt("app_version_code", 940031);
                bundle2.putInt("session_id", 2);
                bundle2.putInt("status", 8);
                bundle2.putInt("error_code", 0);
                bundle2.putLong("bytes_downloaded", 0L);

                bundle2.putLong("total_bytes_to_download:init_pack", 720672983L);
                bundle2.putLong("total_bytes_to_download:dlc_hd", 519569342L);
                bundle2.putLong("bytes_downloaded:init_pack", 0L);
                bundle2.putLong("bytes_downloaded:dlc_hd", 0L);
                bundle2.putLong("pack_version:init_pack", 940031L);
                bundle2.putLong("pack_version:dlc_hd", 940031L);
                bundle2.putLong("pack_base_version:init_pack", 0L);
                bundle2.putLong("pack_base_version:dlc_hd", 0L);
                bundle2.putLong("session_id:init_pack", 8L);
                bundle2.putLong("session_id:dlc_hd", 8L);

                // 返回数据给回调
                callback.onRequestDownloadInfo(bundle1, bundle2);
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
