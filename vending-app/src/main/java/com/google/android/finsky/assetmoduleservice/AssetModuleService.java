package com.google.android.finsky.assetmoduleservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.webkit.internal.ApiFeature;

import org.microg.vending.billing.proto.*;

import com.android.vending.AssetModuleDeliveryRequest;
import com.android.vending.Bbvz;
import com.android.vending.Bcmf;
import com.android.vending.Bdpo;
import com.android.vending.Bdpp;
import com.google.android.play.core.assetpacks.protocol.IAssetModuleService;
import com.google.android.play.core.assetpacks.protocol.IAssetModuleServiceCallback;
import com.squareup.wire.Message;

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
                headers.put("x-ps-rh", "H4sIAAAAAAAAAGVUO4_rxhW29sbAhSpjgQDBbXJjxIARgLt8U3uBFIcciqLE4UukuFSz4EsUHxK1IiWKrAKnSRkg6RK4sJsgTeA6BtK5dOPOhWHkJ7hxcKtw77oIkBnMnA-n-M75PsyZ8e9H49-NZmytwk_rgcMdd1HpJrdka68jicG53-ua2-s97vFWXLKiNeRUsskxg5HU1nl0wb3cyUl-CMOLkHSS3fvxYqHRbmZkLKFtaTPwDkjNp0wf56pWbQBmzc6LVsVTyd_a_3ox_vLF-D8jCfCysWR9ZwUhp1X6_Ror_GLfi6mrnH1ZLn1QmPMxROphXgETIJUCwC6GxdxkL10uWkavWKgDkDYLQCA3qn50KmjaFLy1LIPhG6BFC1AnCuitDPPWgrm8AJFyAEskt5WhtPpg1iIEqaiApi5BWrcgWCYYT4QToFp2VqUuMG3Hi5VrwoKEB4uNU2spixbIw4bC8qFoXRAtyROrago8SCC6q9Oa7iT2tOHSbrZcrxFnOjs5mVDXX76TnoGsh8J516SnGDZdL5p-TC095LGRnVlhXzQtJqNUAeOyrw6VsicJW_TbBWB8AKhgv812iBskGtG9aIIjapMVc2mhOPY7OZzwsJgEhLX2wGAHKfcpzDqMcIUBt_LE8guYGhsRUAkpagb58dQ-PdSar9HdjDukxvkYp7h4rJVS8lgi8W2BOu3Fukav3lt_-mL8lxeDdh8kVXLJeCGn1RCjJYJMPt6daHGL2G0TkrCai9LWvWDksnoOHe4tGjuYxb3LYgdovU8tsCssmRvhErqKjKX706kWcVqreSREDfhWahiElFeKp5TbkKYOVqrnoVcurbZaDRw9zuFiIJXRc3l4mfgyRBrnbj_0FyDINxyWKHErtQ07cyBXsTQP0omAFVmValrzkZHapjsVTStdmy6ypxY5X9rWE7fMGE4xcAGn5wWjO8VF74E0HPnyE7fG8PQTtzWZ71NwVE0y9-7-KA86kJ8hlRfL7cH37prAVWeRclf79_NDRM2nz56oHM7TYc7eYRajaKgB3TB5z54YXlAdB08MNC99Fj15QsW0XaeDJ_hsY_S_nsRF7OmV71GtBxBevXz_-N4no7dX4x-vQJzKDbUKz44ubw62Jh3M7OT4JylN9Hl2Mu6sXaaz0zve0hjGfDhRe9Ux_OlZKLhFf-69IBPrEqv3zfLcaKZAZRShGYVnYZvPTLa02sukSNSQsnuBZ8q9WzTrFLEIhVgThdJUGL2p1kGvT1da5QXMpvApm47XPp8ol0DyHy1Btuc2D7M7MlWKsDwyyiq6-Dy96Es8kR8tfiPny2r5WGsLAQsbDWmyUBHbAyCjakOzFyNFWDjHWtH5bt6xG55hFuFs6sjnHWWiMiXSqZf5E41K1OGPqQ1bKjPqEJTa1q8knYx18tHV7ze2Vc9PYdevN6dafnBr7KzNMi7TNW6baJVa29lyFu3OzTwSEhoiv4nF9EESp7aWqqnt0kX7QDL5mSRXJ45w2M9Gb8a_fPmr62vHpOCGpimS5G5Ikr4R6VdXFPP67Y9_-vj658GOCPbxscpioknKujkGRHD6xejvo89H4_ej6hiU18_h1djMLkn5mn19r31IT7N9XXS3LH1D3dACwVAf0eRHnDgc9IxM-xny3GTCkhw3-c0zDfndn7__xz8_--EL_o2YVlVaJrfv8s_3G4q5_f92b--YCc0w3JtTnRxvj0mZBHVCFElXfzv6ePxrOmRIRhAigqM2IcEmbECEbJAQEZ2QFM-FzB0Vf3D179HPXn7y9ejtaPzB37756x-_-MMPX334X2RsX_qQBgAA");
                headers.put("User-Agent", "Android-Finsky/42.0.20-23 [0] [PR] 654119317 (api=3,versionCode=84202000,sdk=33,device=coral,hardware=coral,product=coral,platformVersionRelease=13,model=Pixel%204%20XL,buildId=TP1A.221005.002.B2,isWideScreen=0,supportedAbis=arm64-v8a;armeabi-v7a;armeabi)");
                headers.put("Authorization", "Bearer ya29.m.CpYCAXQnlZez7qMnpSt8Ld3sbaekzjEX0N72I8JYkAhuuJ07uNYqhlU5-Mlos2RNIKyiD_ZOiSsbHneNQAQJaEUjTe-frJ11POdpZKNueM4qioT6qjqMDhOoxeYEUgqPlkgRLQuWO6tbReVUj7815Id2HdSM7oy6HOXTYPN4-b176Llxg9o1G_Dk6hfth7pnzPVC9JE1_CpFgjJoRuuy0j-YT-q4ibpuj6vlkrrl1HVjJTNmD1BeW3r7kG4ejBjDnFgL3zkcD1IMS5iak2-k1QgzcT87r5_1zIIl3D_7GLKs23cCw0PMXXivZ4TxTcemKc4yCZ6_hWKTXKQs9wXZSctVWSkFS4k5xctb0nggstC9Eodwn_cvf6ESDggBEgcKAQQQjIIBGMBlGiB-404k9co_m4kcYK7zWW92wD25oZCvpPsKeo-jtGSPJCICCAEqK2FDZ1lLQWRjU0FSRVNGUUhHWDJNaUFoQVlLbHd4TmxtODVuM0JDSkhickE");
                headers.put("Accept-Language", "zh-CN");
                headers.put("Content-Type", "application/x-protobuf");
                headers.put("Accept-Encoding", "gzip, deflate, br");
                headers.put("Priority", "u=1, i");


                Log.d(TAG, String.valueOf(requestPayload));
                Log.d(TAG, headers.toString());


                // 发送GET请求
                assetModuleInfo.sendPostRequest("https://play-fe.googleapis.com/fdfe/assetModuleDelivery", requestPayload, headers, new AssetModuleInfo.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "POST Request Success: " + result);
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "POST Request Error: " + error);
                    }
                });
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
