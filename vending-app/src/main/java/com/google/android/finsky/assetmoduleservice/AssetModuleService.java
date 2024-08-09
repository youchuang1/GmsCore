package com.google.android.finsky.assetmoduleservice;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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


import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.Objects;


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
                String versionCode = getAppVersionCode(packageName);

                if (versionCode == null) {
                    Log.d(TAG,versionCode);
                }

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

                List<Bcmf> assetModules = new ArrayList<>();
                for (Bundle b : list) {
                    String moduleName = b.getString("module_name");
                    if (moduleName != null) {
                        assetModules.add(new Bcmf.Builder().b(moduleName).build());
                    }
                }
                requestBuilder.requestedAssetModules(assetModules);

                AssetModuleDeliveryRequest requestPayload = requestBuilder.build();

                Log.d(TAG, String.valueOf(requestPayload));


                AssetModuleRequest request = new AssetModuleRequest(context,"https://play-fe.googleapis.com/fdfe/assetModuleDelivery",
                        requestPayload,user, new AssetModuleRequest.VolleyCallback() {
                    @Override
                    public void onSuccess(byte[] result) {
                        try {
                            IntermediateIntegrityResponseWrapperExtend.IntermediateIntegrityResponseWrapper.AssetModuleDeliveryResponse response = Objects.requireNonNull(IntermediateIntegrityResponseWrapperExtend.ADAPTER.decode(result).intermediateIntegrityResponseWrapper).assetModuleDeliveryResponse;
                            Integer resourceStatus = response.resourceStatus;
                            if (resourceStatus != 0){
                                Bundle errorBundle = new Bundle();
                                errorBundle.putInt("error_code", resourceStatus);
                                callback.onError(errorBundle);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("Error", error);
                    }
                });



                File cacheDir = context.getCacheDir(); // /data/user/0/com.android.vending/cache
                DownloadThreadPool downloadPool = DownloadThreadPool.getInstance(3);

                NetworkRequestManager.getInstance(context).addToRequestQueue(request);

                FileDownloader downloader = new FileDownloader(context);
                String fileUrl = "https://play.googleapis.com/download/by-token/download?token=AOTCm0RLmJtQCX2F7v-YO5yL6HK48tpSBRIQkwsJudzi2LkCrB_dFjTt7ErTgKrbix_sV2hVdtVqlxVVj32q3RWGCMCBOLvwqY9Ba5csJgz7dmaTDXeVbT92i69U-H4yDdQqXEJCYTDObEDViWQfV3C8mQcO21bFTQ3Ap-1bTQEf1wSW8BKAKA9xnwhlSAfAhosu4gnY2IOroP7iWFi_SaybIvHk7Gzr8Yq3pUYsS2zTFaosp6kgy4SK5zcmPxpoESTF2dsdbWvPOzk5Ihc0iCmvVyM8xSh5sTKCvrJuZj03ZT0yWXzdGeM6_UGd0D_DjJIMStU7XO_D1RPGq-fwUfr_IosbkiNIKq1XUtcL7GqRBCMIzISwmUBxjYUN5Y0DKLQnfBwu9PtzAZFAQ3SOfs14rWjW_1e18YcRllq1PsTBg2KOUfVE6THrTTlRzp4G5Msoqrp13Rwf_xecUmESKp5DTSO62jE-0dBzdPR9&cpn=qDqu9jtCXIdZP4XD";
                File destinationFile = new File(cacheDir, "largefile.zip");


                downloader.downloadFile(fileUrl, destinationFile, (totalBytes, downloadedBytes) -> {
                    // 更新 UI 线程或显示下载进度
                    System.out.printf("下载进度: %d/%d 字节%n", downloadedBytes, totalBytes);
                });

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