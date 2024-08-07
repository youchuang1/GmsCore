package com.google.android.finsky.assetmoduleservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import com.android.vending.licensing.LicenseRequest;

import com.android.vending.AssetModuleDeliveryRequest;
import com.android.vending.Bbvz;
import com.android.vending.Bcmf;
import com.android.vending.Bdpo;
import com.android.vending.Bdpp;
import com.android.vending.IntermediateIntegrityResponseWrapperExtend;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
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
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        // 初始化 AssetModuleInfo 实例
        assetModuleInfo = new AssetModuleInfo(this, getPackageManager(), new AssetModuleController(), new AuthenticationHandler(), new OwnershipChecker());

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

                Map<String, String> headers = new HashMap<>();
                headers.put("x-ps-rh", "H4sIAAAAAAAAAGVUO4_j1hW2ZmNgocoYwECwTdZGDBgGOMM3NQukOOSlKEq8fIkUh2oGfIniQ6JGpESRlWE3KQM4XYIUThOkCVzHQIAU-QPpUgRBfoKbDbYKZ8dFgNyLe8-HU3znfB_uueOvR-MvRzO2VuHH9cDhjruodJNbsrXXkcTg3O91ze31Hvd4Ky5Z0RpyKtnkmMFIaus8uuBe7uQkP4ThRUg6ye79eLHQaDczMpbQtrQZeAek5lOmj3NVqzYAs2bnRaviqeQv7L-8GH__YvyfkQR42ViyvrOCkNMq_X6NFX6x78XUVc6-LJc-KMz5GCL1MK-ACZBKAWAXw2JuspcuFy2jVyzUAUibBSCQG1U_OhU0bQreWpbB8A3QogWoEwX0VoZ5a8FcXoBIOYAlktvKUFp9MGsRglRUQFOXIK1bECwTjCfCCVAtO6tSF5i248XKNWFBwoPFxqm1lEUL5GFDYflQtC6IluSJVTUFHiQQ3dVpTXcSe9pwaTdbrteIM52dnEyo6-_fS89A1kPhvGvSUwybrhdNP6aWHvLYyM6ssC-aFpNRqoBx2VeHStmThC367QIwPgBUsN9mO8QNEo3oXjTBEbXJirm0UBz7nRxOeFhMAsJae2Cwg5T7FGYdRrjCgFt5YvkFTI2NCKiEFDWD_Hhqnx5qzdfobsYdUuN8jFNcPNZKKXkskfi2QJ32Yl2jVx-sf_di_JsXg3YfJFVyyXghp9UQoyWCTD7enWhxi9htE5KwmovS1r1g5LJ6Dh3uLRo7mMW9y2IHaL1PLbArLJkb4RK6ioyl-9OpFnFaq3kkRA34VmoYhJRXiqeU25CmDlaq56FXLq22Wg0cPc7hYiCV0XN5eJn4MkQa524_9BcgyDcclihxK7UNO3MgV7E0D9KJgBVZlWpa85GR2qY7FU0rXZsusqcWOV_a1hO3zBhOMXABp-cFozvFRe-BNBz58iO3xvD0E7c1me9TcFRNMvfu_igPOpCfIZUXy-3B9-6awFVnkXJX-_fzQ0TNp8-eqBzO02HO3mMWo2ioAd0wec-eGF5QHQdPDDQvfRY9eULFtF2ngyf4bGP0v57ERezple9RrQcQXr388PjBV6N3V-O3VyBO5YZahWdHlzcHW5MOZnZy_JOUJvo8Oxl31i7T2ekdb2kMYz6cqL3qGP70LBTcoj_3XpCJdYnV-2Z5bjRToDKK0IzCs7DNZyZbWu1lUiRqSNm9wDPl3i2adYpYhEKsiUJpKozeVOug16crrfICZlP4lE3Ha59PlEsg-Y-WINtzm4fZHZkqRVgeGWUVXXyeXvQlnsiPFr-R82W1fKy1hYCFjYY0WaiI7QGQUbWh2YuRIiycY63ofDfv2A3PMItwNnXk844yUZkS6dTL_IlGJerwx9SGLZUZdQhKbetXkk7GOvno6vcb26rnp7Dr15tTLT-4NXbWZhmX6Rq3TbRKre1sOYt252YeCQkNkd_EYvogiVNbS9XUdumifSCZ_EySqxNHOOy3ozfjn7385PraMSm4oWmKJLkbkqRvRPrVFcW8fvf2m8-vPw52RLCPj1UWE01S1s0xIILTT0d_HP1-NP4wqo5Bef0cXo3N7JKUr9nX99qn9DTb10V3y9I31A0tEAz1GU1-xonDQc_ItJ8hz00mLMlxky-each__vpff_rztz98x78R06pKy-T2ff75fkMxt__f7u0dM6EZhntzqpPj7TEpk6BOiCLp6n-MPh__nA4ZkhGEiOCoTUiwCRsQIRskREQnJMVzIXNHxR9d_Xv0k5dv_zp6Nxp_9Ie___ZX3_3yh799-l-Pq5UakAYAAA");
                headers.put("User-Agent", "Android-Finsky/42.0.20-23 [0] [PR] 654119317 (api=3,versionCode=84202000,sdk=33,device=coral,hardware=coral,product=coral,platformVersionRelease=13,model=Pixel%204%20XL,buildId=TP1A.221005.002.B2,isWideScreen=0,supportedAbis=arm64-v8a;armeabi-v7a;armeabi)");
                headers.put("Authorization", "Bearer ya29.m.CpYCAXQnlZez7qMnpSt8Ld3sbaekzjEX0N72I8JYkAhuuJ07uNYqhlU5-Mlos2RNIKyiD_ZOiSsbHneNQAQJaEUjTe-frJ11POdpZKNueM4qioT6qjqMDhOoxeYEUgqPlkgRLQuWO6tbReVUj7815Id2HdSM7oy6HOXTYPN4-b176Llxg9o1G_Dk6hfth7pnzPVC9JE1_CpFgjJoRuuy0j-YT-q4ibpuj6vlkrrl1HVjJTNmD1BeW3r7kG4ejBjDnFgL3zkcD1IMS5iak2-k1QgzcT87r5_1zIIl3D_7GLKs23cCw0PMXXivZ4TxTcemKc4yCZ6_hWKTXKQs9wXZSctVWSkFS4k5xctb0nggstC9Eodwn_cvf6ESDwgBEgcKAQQQm_8BGM_iARogAPElGwo0FCG9kmJ-GH20Ddi94ajQ05R5-9wk6YqBhl4iAggBKithQ2dZS0FkY1NBUkVTRlFIR1gyTWlBaEFZS2x3eE5sbTg1bjNCQ0pIYnJB");
                headers.put("Accept-Language", "zh-CN");
                headers.put("Content-Type", "application/x-protobuf");
                headers.put("Accept-Encoding", "gzip, deflate, br");
                headers.put("Priority", "u=1, i");


                Log.d(TAG, String.valueOf(requestPayload));
                Log.d(TAG, headers.toString());


                AssetModuleRequest request = new AssetModuleRequest("https://play-fe.googleapis.com/fdfe/assetModuleDelivery",
                        requestPayload, headers, new AssetModuleRequest.VolleyCallback() {
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
