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
import android.os.RemoteException;

import android.util.Log;

import com.android.vending.AssetModuleDeliveryRequest;
import com.android.vending.Bbvz;
import com.android.vending.Bcmf;
import com.android.vending.Bdpo;
import com.android.vending.Bdpp;
import com.android.vending.IntermediateIntegrityResponseWrapperExtend;


import com.google.android.play.core.assetpacks.model.AssetPackStatus;
import com.google.android.play.core.assetpacks.model.StringUtil;
import com.google.android.play.core.assetpacks.protocol.IAssetModuleService;
import com.google.android.play.core.assetpacks.protocol.IAssetModuleServiceCallback;


import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.Objects;
import java.util.Set;


public class AssetModuleService extends Service {
    private static final String TAG = "AssetModuleService";
    private AssetModuleInfo assetModuleInfo;
    public Context context;
    private Account user;
    private final String assetModuleDelivery_URL = "https://play-fe.googleapis.com/fdfe/assetModuleDelivery";
    private AppData appData;


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        assetModuleInfo = new AssetModuleInfo(this, getPackageManager(), new AssetModuleController(), new AuthenticationHandler(), new OwnershipChecker());
        user = getAccount();
        appData = new AppData();
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
                // 延迟一秒
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (Bundle b : list) {
                    String moduleName = b.getString("module_name");
                    ArrayList<String> packNames = new ArrayList<>();
                    packNames.add(moduleName);
                    AppData.PackData packData = appData.getPackData(moduleName);
                    Bundle bundle_data = new Bundle();
                    bundle_data.putInt("session_id:" + moduleName, packData.getSessionId());
                    bundle_data.putInt("status:" + moduleName, AssetPackStatus.PENDING);
                    bundle_data.putInt("error_code:" + moduleName, packData.getErrorCode());
                    bundle_data.putLong("pack_version:" + moduleName, packData.getPackVersion());
                    bundle_data.putLong("pack_base_version:" + moduleName, packData.getPackBaseVersion());
                    bundle_data.putLong("bytes_downloaded:" + moduleName, packData.getBytesDownloaded());
                    bundle_data.putLong("total_bytes_to_download:" + moduleName, packData.getTotalBytesToDownload());
                    bundle_data.putStringArrayList("pack_names", packNames);
                    bundle_data.putInt("status", AssetPackStatus.PENDING);
                    bundle_data.putInt("app_version_code", appData.getAppVersionCode());
                    bundle_data.putLong("total_bytes_to_download", packData.getTotalBytesToDownload());
                    bundle_data.putInt("error_code", appData.getErrorCode());
                    bundle_data.putInt("session_id", appData.getSessionId());
                    bundle_data.putLong("bytes_downloaded", appData.getBytesDownloaded());

                    callback.onStartDownload(-1, bundle_data);

                    appData.setStatus(AssetPackStatus.DOWNLOADING);
                    packData.setStatus(AssetPackStatus.DOWNLOADING);
                    packData.setSessionId(AssetPackStatus.DOWNLOADING);


                    for (Bundle bundle_datas : packData.getBundleList()) {
                        downloadFile(moduleName, bundle_datas);
                    }
                }
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

                if (!packageName.equals(appData.getPackageName())) {
                    appData.initialize();
                }

                if (appData.getPackNames() != null && !appData.getPackNames().isEmpty()) {
                    Bundle bundle_data = new Bundle();
                    bundle_data.putStringArrayList("pack_names", appData.getPackNames());
                    bundle_data.putInt("error_code", appData.getErrorCode());
                    bundle_data.putInt("session_id", appData.getSessionId());
                    bundle_data.putLong("bytes_downloaded", appData.getBytesDownloaded());
                    bundle_data.putInt("status", appData.getStatus());
                    bundle_data.putInt("app_version_code", appData.getAppVersionCode());
                    bundle_data.putLong("total_bytes_to_download", appData.getTotalBytesToDownload());

                    // 添加每个包的数据
                    for (String packName : appData.getPackNames()) {
                        AppData.PackData packData = appData.getPackData(packName);
                        if (packData != null) {
                            bundle_data.putInt("session_id:" + packName, packData.getSessionId());
                            bundle_data.putInt("status:" + packName, packData.getStatus());
                            bundle_data.putInt("error_code:" + packName, packData.getErrorCode());
                            bundle_data.putLong("pack_version:" + packName, packData.getPackVersion());
                            bundle_data.putLong("pack_base_version:" + packName, packData.getPackBaseVersion());
                            bundle_data.putLong("bytes_downloaded:" + packName, packData.getBytesDownloaded());
                            bundle_data.putLong("total_bytes_to_download:" + packName, packData.getTotalBytesToDownload());
                        }
                    }

                    Log.d("sssss", bundle_data.toString());
                    callback.onRequestDownloadInfo(bundle_data, bundle_data);
                    return;
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

//                Log.d("AppData", String.valueOf(requestPayload));


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

        List<String> pack_names = new ArrayList<>();
        Long total_bytes_to_download = 0L;
        int app_version_code = 0;
        int session_id = 0;
        int error_code = 0;
        int bytes_downloaded = 0;

        for (int i = 0; i < resourceList.size(); i++) {
            IntermediateIntegrityResponseWrapperExtend.IntermediateIntegrityResponseWrapper.Bbvy resource = resourceList.get(i);
            String resourcePackageName = resource.resourcePackageName;
            ArrayList<Bundle> bundlePackageName = new ArrayList<>();

            long total_bytes_to_downloads = 0L;

            app_version_code = Math.toIntExact(resource.versionNumber);
            pack_names.add(resourcePackageName);
            List<IntermediateIntegrityResponseWrapperExtend.IntermediateIntegrityResponseWrapper.Bbwb> fList = resource.f;
            session_id = i;
            for (int j = 0; j < fList.size(); j++) {
                IntermediateIntegrityResponseWrapperExtend.IntermediateIntegrityResponseWrapper.Bbwb fResource = fList.get(j);
                String chunkName = fResource.b.a;
                List<IntermediateIntegrityResponseWrapperExtend.IntermediateIntegrityResponseWrapper.Bbvx> dList = fResource.c.d;
                for (int x = 0; x < dList.size(); x++) {
                    IntermediateIntegrityResponseWrapperExtend.IntermediateIntegrityResponseWrapper.Bbvx dResource = dList.get(x);
                    String resourceLink = dResource.resourceLink;
                    total_bytes_to_download += dResource.byteLength;
                    total_bytes_to_downloads += dResource.byteLength;
                    String Index = String.valueOf(i);
                    String resourceBlockName = String.valueOf(x);
                    Bundle bundle = new Bundle();
                    bundle.putString("CacheDir", String.valueOf(context.getCacheDir()));
                    bundle.putInt("index", Integer.parseInt(Index));
                    bundle.putString("resourcePackageName", resourcePackageName);
                    bundle.putString("chunkName", chunkName);
                    bundle.putString("resourceLink", resourceLink);
                    bundle.putLong("byteLength", Math.toIntExact(dResource.byteLength));
                    bundle.putString("resourceBlockName", resourceBlockName);
//                    Log.d("AppData", String.valueOf(bundle));
                    bundlePackageName.add(bundle);
                }
            }
            AppData.PackData packData = new AppData.PackData();
            packData.setPackVersion(Math.toIntExact(resource.versionNumber));
            packData.setPackBaseVersion(0);
            packData.setSessionId(i);
            packData.setErrorCode(0);
            packData.setStatus(8);
            packData.setBytesDownloaded(0);
            packData.setTotalBytesToDownload(total_bytes_to_downloads);
            packData.setBundleList(bundlePackageName);
            appData.addPackData(resourcePackageName, packData);
        }

        appData.setPackNames((ArrayList<String>) pack_names);
        appData.setTotalBytesToDownload(total_bytes_to_download);
        appData.setAppVersionCode(app_version_code);
        appData.setSessionId(session_id);
        appData.setStatus(AssetPackStatus.NOT_INSTALLED);
        appData.setErrorCode(error_code);
        appData.setBytesDownloaded(bytes_downloaded);
        appData.setPackageName(packageName);
    }


    private void downloadFile(String packageName, Bundle bundle) {
        String tag = "downloadFile";
        String resourcePackageName = bundle.getString("resourcePackageName");
        String chunkName = bundle.getString("chunkName");
        String resourceLink = bundle.getString("resourceLink");
        long byteLength = bundle.getLong("byteLength");
        String resourceBlockName = bundle.getString("resourceBlockName");
        String Index = String.valueOf(bundle.getInt("index"));
        Log.d(tag, "resourceLink:" + resourceLink + ", URL: " + ",byteLength:" + byteLength + ", resourceBlockName:" + resourceBlockName);
        String cacheDir = String.valueOf(context.getCacheDir()) + "/" + Index + "/" + resourcePackageName + "/" + chunkName;
        FileDownloader fileDownloader = new FileDownloader(this);
        File destination = new File(cacheDir, resourceBlockName);
        fileDownloader.downloadFile(resourceLink, destination, new FileDownloader.ProgressListener() {

            @Override
            public void onError(Exception e) {
                // 处理错误
                e.printStackTrace();
            }

            @Override
            public void onComplete() {
                appData.incrementPackBytesDownloaded(packageName, byteLength);
                appData.incrementBytesDownloaded(byteLength);
                Log.d(tag, "bytesDownloaded:" + String.valueOf(appData.getPackField(packageName, "bytesDownloaded")));
                Log.d(tag, "getBytesDownloaded:" + String.valueOf(appData.getBytesDownloaded()));

                try {
                    int status = AssetPackStatus.DOWNLOADING;
                    int version = Integer.parseInt(getAppVersionCode("com.gameloft.android.ANMP.GloftDYHM"));
                    Bundle uBundle = new Bundle();
                    uBundle.putInt("app_version_code", version);
                    uBundle.putInt("error_code", 0);
                    uBundle.putInt("session_id", appData.getSessionId()); //TODO
                    uBundle.putInt("status", status);
                    String[] stringArray = new String[]{resourcePackageName};
                    uBundle.putStringArrayList("pack_names", new ArrayList(Arrays.asList(stringArray)));
                    uBundle.putLong("bytes_downloaded", 5675);//TODO
                    uBundle.putLong("total_bytes_to_download", 521390713);//TODO

                    uBundle.putLong(StringUtil.combine("total_bytes_to_download", resourcePackageName), 521390713);//TODO
                    ArrayList uArrayList = new ArrayList<>(Arrays.asList(resourcePackageName, chunkName));
                    uBundle.putStringArrayList(StringUtil.combine("slice_ids", resourcePackageName), uArrayList);
                    uBundle.putLong(StringUtil.combine("pack_version", resourcePackageName), version);
                    uBundle.putInt(StringUtil.combine("status", resourcePackageName), status);
                    uBundle.putInt(StringUtil.combine("error_code", resourcePackageName), 0);
                    uBundle.putLong(StringUtil.combine("bytes_downloaded", resourcePackageName), 5675);//TODO
                    uBundle.putString(StringUtil.combine("pack_version_tag", resourcePackageName), "");
                    uBundle.putLong(StringUtil.combine("pack_base_version", resourcePackageName), 0);
                    uBundle.putLong(StringUtil.combine("uncompressed_size", resourcePackageName, chunkName), 522126649);//TODO
                    uBundle.putInt(StringUtil.combine("compression_format", resourcePackageName, chunkName), 1);

                    ArrayList uArrayList2 = new ArrayList(10);//TODO
                    uBundle.putParcelableArrayList(StringUtil.combine("chunk_intents", resourcePackageName, chunkName), uArrayList2);
                    uBundle.putString(StringUtil.combine("uncompressed_hash_sha256", resourcePackageName, chunkName), "S7lwNUmJRFAiidbZjm9cOFAjOoYPg65PKugxRiKqt4E");//TODO

                    uBundle.putLong(StringUtil.combine("uncompressed_size", resourcePackageName, resourcePackageName), 12637);//TODO
                    uBundle.putInt(StringUtil.combine("compression_format", resourcePackageName, resourcePackageName), 1);
                    ArrayList uArrayList3 = new ArrayList();//TODO

                    Uri uri = Uri.parse(destination.getAbsolutePath());
                    Intent intent = new Intent().setData(uri);
                    uArrayList3.add(intent);

                    uBundle.putParcelableArrayList(StringUtil.combine("chunk_intents", resourcePackageName, resourcePackageName), uArrayList3);
                    uBundle.putString(StringUtil.combine("uncompressed_hash_sha256", resourcePackageName, resourcePackageName), "PQ0MNdYvDMTYFXQelv0exchwC9iD502E3wnTBCQzDZ0");//TODO
                    Log.d(tag, "bundleToString:" + bundleToString(uBundle));
                    sendBroadCast(uBundle);
                } catch (Exception e) {
                    Log.d(tag, "bundleToString:" + e.getMessage());
                }
            }
        });
    }

    private void sendBroadCast(Bundle obj) {
        Intent intent = new Intent();
        intent.setAction("com.google.android.play.core.assetpacks.receiver.ACTION_SESSION_UPDATE");
        intent.putExtra("com.google.android.play.core.assetpacks.receiver.EXTRA_SESSION_STATE", obj);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Bundle bundle = new Bundle();
        bundle.putBoolean("usingExtractorStream", true);
        intent.putExtra("com.google.android.play.core.FLAGS", bundle);
        intent.setPackage("com.gameloft.android.ANMP.GloftDYHM");
        context.sendBroadcast(intent);
    }

    public static String bundleToString(Bundle bundle) {
        if (bundle == null) {
            return "Bundle is null";
        }
        StringBuilder sb = new StringBuilder("Bundle: ");
        Set<String> keySet = bundle.keySet();
        for (String key : keySet) {
            Object value = bundle.get(key);
            sb.append(key).append("=").append(value).append("\n");
        }
        return sb.toString();
    }
}