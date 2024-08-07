package com.google.android.finsky.assetmoduleservice;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.util.Base64;

import com.android.vending.AndroidVersionMeta;
import com.android.vending.AssetModuleDeliveryRequest;
import com.android.vending.DeviceMeta;
import com.android.vending.EncodedTriple;
import com.android.vending.EncodedTripleWrapper;
import com.android.vending.IntWrapper;
import com.android.vending.LicenseRequestHeader;
import com.android.vending.Locality;
import com.android.vending.LocalityWrapper;
import com.android.vending.StringWrapper;
import com.android.vending.Timestamp;
import com.android.vending.TimestampContainer;
import com.android.vending.TimestampContainer1;
import com.android.vending.TimestampContainer1Wrapper;
import com.android.vending.TimestampContainer2;
import com.android.vending.TimestampStringWrapper;
import com.android.vending.TimestampWrapper;
import com.android.vending.UnknownByte12;
import com.android.vending.UserAgent;
import com.android.vending.Util;
import com.android.vending.Uuid;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.microg.gms.profile.Build;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okio.ByteString;

public final class AssetModuleRequest extends Request<byte[]> {
    private final AssetModuleDeliveryRequest requestPayload;
    private final VolleyCallback callback;
    private static final String tokentype = "oauth2:https://www.googleapis.com/auth/googleplay";
    private final Account user;
    private final Context context;
    private static final int BASE64_FLAGS = Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING;
    long ANDROID_ID = 1;
    private static final String FINSKY_VERSION = "Finsky/37.5.24-29%20%5B0%5D%20%5BPR%5D%20565477504";

    public AssetModuleRequest(Context context, String url, AssetModuleDeliveryRequest requestPayload, Account user, VolleyCallback callback) {
        super(Method.POST, url, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        });
        this.context = context;
        this.requestPayload = requestPayload;
        this.user = user;
        this.callback = callback;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Locality locality = new Locality.Builder()
                .unknown1(1)
                .unknown2(2)
                .countryCode("")
                .region(new TimestampStringWrapper.Builder()
                        .string("").timestamp(makeTimestamp(System.currentTimeMillis())).build())
                .country(new TimestampStringWrapper.Builder()
                        .string("").timestamp(makeTimestamp(System.currentTimeMillis())).build())
                .unknown3(0)
                .build();
        String encodedLocality = new String(
                Base64.encode(locality.encode(), BASE64_FLAGS)
        );
        long millis = System.currentTimeMillis();
        TimestampContainer.Builder timestamp = new TimestampContainer.Builder()
                .container2(new TimestampContainer2.Builder()
                        .wrapper(new TimestampWrapper.Builder().timestamp(makeTimestamp(millis)).build())
                        .timestamp(makeTimestamp(millis))
                        .build());
        millis = System.currentTimeMillis();
        timestamp
                .container1Wrapper(new TimestampContainer1Wrapper.Builder()
                        .androidId(String.valueOf(ANDROID_ID))
                        .container(new TimestampContainer1.Builder()
                                .timestamp(millis + "000")
                                .wrapper(makeTimestamp(millis))
                                .build())
                        .build()
                );
        String encodedTimestamps = new String(
                Base64.encode(Util.encodeGzip(timestamp.build().encode()), BASE64_FLAGS)
        );
        byte[] header = new LicenseRequestHeader.Builder()
                .encodedTimestamps(new StringWrapper.Builder().string(encodedTimestamps).build())
                .triple(
                        new EncodedTripleWrapper.Builder().triple(
                                new EncodedTriple.Builder()
                                        .encoded1("")
                                        .encoded2("")
                                        .empty("")
                                        .build()
                        ).build()
                )
                .locality(new LocalityWrapper.Builder().encodedLocalityProto(encodedLocality).build())
                .unknown(new IntWrapper.Builder().integer(5).build())
                .empty("")
                .deviceMeta(new DeviceMeta.Builder()
                        .android(
                                new AndroidVersionMeta.Builder()
                                        .androidSdk(Build.VERSION.SDK_INT)
                                        .buildNumber(Build.ID)
                                        .androidVersion(Build.VERSION.RELEASE)
                                        .unknown(0)
                                        .build()
                        )
                        .unknown1(new UnknownByte12.Builder().bytes(new ByteString(new byte[]{}
                        )).build())
                        .unknown2(1)
                        .build()
                )
                .userAgent(new UserAgent.Builder()
                        .deviceName(Build.DEVICE)
                        .deviceHardware(Build.HARDWARE)
                        .deviceModelName(Build.MODEL)
                        .finskyVersion(FINSKY_VERSION)
                        .deviceProductName(Build.MODEL)
                        .androidId(ANDROID_ID) // must not be 0
                        .buildFingerprint(Build.FINGERPRINT)
                        .build()
                )
                .uuid(new Uuid.Builder()
                        .uuid(UUID.randomUUID().toString())
                        .unknown(2)
                        .build()
                )
                .build().encode();
        String xPsRh = new String(Base64.encode(Util.encodeGzip(header), BASE64_FLAGS));
        Map<String, String> headers = new HashMap<>();
        headers.put("x-ps-rh", xPsRh);
        headers.put("User-Agent", "Android-Finsky/42.0.20-23 [0] [PR] 654119317 (api=3,versionCode=84202000,sdk=33,device=coral,hardware=coral,product=coral,platformVersionRelease=13,model=Pixel%204%20XL,buildId=TP1A.221005.002.B2,isWideScreen=0,supportedAbis=arm64-v8a;armeabi-v7a;armeabi)");
        try {
            headers.put("Authorization", "Bearer " + AccountManager.get(context).getAuthToken(user,tokentype,null,false,null,null).getResult().getString(AccountManager.KEY_AUTHTOKEN));
        } catch (AuthenticatorException | OperationCanceledException | IOException e) {
            throw new RuntimeException(e);
        }
        headers.put("Accept-Language", "zh-CN");
        headers.put("Content-Type", "application/x-protobuf");
        return headers;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return AssetModuleDeliveryRequest.ADAPTER.encode(requestPayload);
    }

    private static Timestamp makeTimestamp(long millis) {
        return new Timestamp.Builder()
                .seconds((millis / 1000))
                .nanos(Math.floorMod(millis, 1000) * 1000000)
                .build();
    }

    @Override
    public String getBodyContentType() {
        return "application/x-protobuf";
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(byte[] response) {
        callback.onSuccess(response);
    }

    // 定义一个接口用于回调请求结果
    public interface VolleyCallback {
        void onSuccess(byte[] result);
        void onError(String error);
    }
}