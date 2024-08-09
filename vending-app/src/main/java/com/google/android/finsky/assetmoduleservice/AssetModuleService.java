package com.google.android.finsky.assetmoduleservice;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
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
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class AssetModuleService extends Service {
    private static final String TAG = "AssetModuleService";
    private AssetModuleInfo assetModuleInfo;
    public Context context;
    private Account user;
    private final String assetModuleDelivery_URL = "https://play-fe.googleapis.com/fdfe/assetModuleDelivery";


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
                callback.onNotifyChunkTransferred(bundle, new Bundle());
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
                callback.onNotifyModuleCompleted(bundle,new Bundle());
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
                Bundle bundle1 = new Bundle();
                bundle.putBoolean("keep_alive", true);
                callback.onKeepAlive(bundle1, new Bundle());
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
                ParcelFileDescriptor parcelFileDescriptor0;
                // todo 要下载的文件
                String downLoadFile = "";
                String s4 = Uri.parse(downLoadFile).getPath();
                File file0 = new File(s4);
                try {
                    parcelFileDescriptor0 = ParcelFileDescriptor.open(file0, ParcelFileDescriptor.MODE_READ_ONLY);
                }
                catch(FileNotFoundException unused_ex) {
                    return;
                }
                Bundle bundle0 = new Bundle();
                bundle0.putParcelable("chunk_file_descriptor", parcelFileDescriptor0);
                callback.onKeepAlive(bundle0, new Bundle());
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
                    Log.d(TAG, versionCode);
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


                AssetModuleRequest request = new AssetModuleRequest(context, assetModuleDelivery_URL,
                        requestPayload, user, new AssetModuleRequest.VolleyCallback() {
                    @Override
                    public void onSuccess(byte[] result) {
                        try {
                            IntermediateIntegrityResponseWrapperExtend.IntermediateIntegrityResponseWrapper.AssetModuleDeliveryResponse response = Objects.requireNonNull(IntermediateIntegrityResponseWrapperExtend.ADAPTER.decode(result).intermediateIntegrityResponseWrapper).assetModuleDeliveryResponse;
                            Integer resourceStatus = response.resourceStatus;
                            if (resourceStatus != null) {
                                Bundle errorBundle = new Bundle();
                                errorBundle.putInt("error_code", resourceStatus);
                                callback.onError(errorBundle);
                            }
                            downloadResources(packageName, response);
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
                NetworkRequestManager.getInstance(context).addToRequestQueue(request);
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


    public void downloadResources(String packageName, IntermediateIntegrityResponseWrapperExtend.IntermediateIntegrityResponseWrapper.AssetModuleDeliveryResponse dataList) {
        List<IntermediateIntegrityResponseWrapperExtend.IntermediateIntegrityResponseWrapper.Bbvy> resourceList = dataList.resourceList;
        for (int i = 0; i < resourceList.size(); i++) {
            IntermediateIntegrityResponseWrapperExtend.IntermediateIntegrityResponseWrapper.Bbvy resource = resourceList.get(i);
            String resourcePackageName = resource.resourcePackageName;
            List<IntermediateIntegrityResponseWrapperExtend.IntermediateIntegrityResponseWrapper.Bbwb> fList = resource.f;

            for (int j = 0; j < fList.size(); j++) {
                IntermediateIntegrityResponseWrapperExtend.IntermediateIntegrityResponseWrapper.Bbwb fResource = fList.get(j);
                String aaa = fResource.b.a;

                List<IntermediateIntegrityResponseWrapperExtend.IntermediateIntegrityResponseWrapper.Bbvx> dList = fResource.c.d;

                for (int x = 0; x < dList.size(); x++) {
                    IntermediateIntegrityResponseWrapperExtend.IntermediateIntegrityResponseWrapper.Bbvx dResource = dList.get(x);
                    String resourceLink = dResource.resourceLink;
                    int byteLength = Math.toIntExact(dResource.byteLength);
                    String Index = String.valueOf(i);
                    String resourceBlockName = String.valueOf(x);
                    String cacheDir = String.valueOf(context.getCacheDir()) + "/" + Index + "/" + resourcePackageName + "/" + aaa;

                    downloadFile(cacheDir, resourceLink, byteLength, resourceBlockName);
                }
            }
        }
    }

    private void downloadFile(String resourceLink, String URL, int byteLength, String resourceBlockName) {
//        Log.d("sssss2", "resourceLink:" + resourceLink + ", URL: " + URL + "byteLength:" + byteLength + ", resourceBlockName:" + resourceBlockName);

        FileDownloader fileDownloader = new FileDownloader(this);
        File destination = new File(resourceLink, resourceBlockName);

        fileDownloader.downloadFile(URL, destination, new FileDownloader.ProgressListener() {
            @Override
            public void onProgress(long totalBytes, long downloadedBytes) {
                // 更新进度，例如更新进度条
                Log.d("DownloadProgress", "Downloaded " + downloadedBytes + " of " + totalBytes);
            }

            @Override
            public void onError(Exception e) {
                // 处理错误
                e.printStackTrace();
            }
        });

    }
}