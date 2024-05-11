/*
 * SPDX-FileCopyrightText: 2023 microG Project Team
 * SPDX-License-Identifier: Apache-2.0
 */

package org.microg.gms.appinivite

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.os.bundleOf
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import kotlinx.coroutines.CompletableDeferred
import okio.ByteString.Companion.decodeHex
import org.json.JSONObject
import org.microg.gms.appinvite.*
import org.microg.gms.common.Constants
import org.microg.gms.utils.singleInstanceOf
import java.util.*
import android.net.Uri
private const val TAG = "AppInviteActivity"  // 日志标签，用于识别来自此活动的日志信息。

// 下面的常量用于定义Intent中使用的键名，这些键名用于在不同组件间传递App邀请相关的数据。
private const val APPINVITE_DEEP_LINK =
    "com.google.android.gms.appinvite.DEEP_LINK"  // 用于传递深链接的键名，深链接直接导航到应用内特定页面。
private const val APPINVITE_INVITATION_ID =
    "com.google.android.gms.appinvite.INVITATION_ID"  // 用于传递邀请ID的键名，标识具体的邀请事件。
private const val APPINVITE_OPENED_FROM_PLAY_STORE =
    "com.google.android.gms.appinvite.OPENED_FROM_PLAY_STORE"  // 用于标识应用是否是从Play商店通过邀请链接打开的。
private const val APPINVITE_REFERRAL_BUNDLE =
    "com.google.android.gms.appinvite.REFERRAL_BUNDLE"  // 用于传递整个引荐数据包的键名，包含所有相关数据。


class AppInviteActivity : AppCompatActivity() {
    private val queue by lazy { singleInstanceOf { Volley.newRequestQueue(applicationContext) } }

    private val Int.px: Int get() = (this * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(ProgressBar(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(20.px)
            isIndeterminate = true
        })
        val extras = intent.extras
        var referrerUrl: String? = null

        extras?.keySet()?.forEach { key ->
            Log.d(TAG, "Extra key: $key, value: ${extras.get(key)}")
            if (key == "android.intent.extra.REFERRER") {
                referrerUrl = extras.get(key).toString()  // 从 extras 获取 referrerUrl
            }
        }
        
        // 检查 Intent 数据是否存在
        if (intent?.data == null) return finish()

        // 检查 referrer 值并根据其值决定运行哪个函数
        if (referrerUrl == "https://www.google.co.uk/" || referrerUrl == "https://www.google.com/") {
            lifecycleScope.launchWhenStarted { run2() }
        } else {
            lifecycleScope.launchWhenStarted { run() }
        }
    }




    private fun redirectToBrowser() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                data = intent.data
            })
        } catch (e: Exception) {
            Log.w(TAG, e)
        }
        finish()
    }

    private fun open(appInviteLink: MutateAppInviteLinkResponse) {
        // 创建一个视图（VIEW）操作的Intent，指定为默认类别
        val intent = Intent(Intent.ACTION_VIEW).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            // 设置数据URI，该URI来自appInviteLink的intentData字段，如果存在的话
            data = appInviteLink.data_?.intentData?.let { Uri.parse(it) }
            // 指定启动特定包名的应用程序
            `package` = appInviteLink.data_?.packageName
            // 设置标志，以新任务的形式打开应用，并清除任务栈顶部的其他活动
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            // 添加额外的信息到Intent中，包括邀请深链接、邀请ID和标记是否从Play商店打开
            putExtra(
                APPINVITE_REFERRAL_BUNDLE, bundleOf(
                    APPINVITE_DEEP_LINK to appInviteLink,
                    APPINVITE_INVITATION_ID to "",
                    APPINVITE_OPENED_FROM_PLAY_STORE to false
                )
            )
        }

        // 创建一个备用的Intent，用于在无法使用主Intent时作为后备
        val fallbackIntent = Intent(Intent.ACTION_VIEW).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
            // 设置数据URI，该URI来自appInviteLink的fallbackUrl字段，如果存在的话
            data = appInviteLink.data_?.fallbackUrl?.let { Uri.parse(it) }
        }

        // 尝试解析安装的应用版本号，并与所需的最小版本号进行比较
        val installedVersionCode = runCatching {
            intent.resolveActivity(packageManager)?.let {
                PackageInfoCompat.getLongVersionCode(
                    packageManager.getPackageInfo(
                        it.packageName,
                        0
                    )
                )
            }
        }.getOrNull()

        // 判断是否应启动主Intent或备用Intent
        if (installedVersionCode != null && (appInviteLink.data_?.app?.minAppVersion == null || installedVersionCode >= appInviteLink.data_.app.minAppVersion)) {
            // 如果已安装应用版本符合要求，启动主Intent
            startActivity(intent)
            finish()  // 关闭当前活动
        } else {
            try {
                startActivity(fallbackIntent)
            } catch (e: Exception) {
                Log.w(TAG, e)
            }
            finish()
        }
    }

    private fun openDynamicLink(response: JSONObject) {
        val linkData = AppInviteLinkData(
            deepLink = response.getString("deepLink"),
            targetAndroidPackage = response.getString("targetAndroidPackage"),
            minAndroidAppVersionCode = response.getInt("minAndroidAppVersionCode"),
            fallbackUri = response.getString("fallbackUri"),
            resolvedLink = response.getString("resolvedLink")
        )

        // 创建视图（VIEW）操作的 Intent
        val deepLinkIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(linkData.deepLink)
            setPackage(linkData.targetAndroidPackage)  // 如果你想指定打开链接的应用
            addCategory(Intent.CATEGORY_DEFAULT)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        // 判断是否应启动主 Intent 或备用 Intent
        try {
            if (deepLinkIntent.resolveActivity(packageManager) != null) {
                startActivity(deepLinkIntent)
                finish()
            } else {
                // 没有应用可以处理这个意图，你可以选择打开网页或提示用户
                Log.e(TAG, "No application available to open deep link: ${linkData.deepLink}")
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(linkData.fallbackUri)))
                finish()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing the deep link: ${e.message}", e)
        }
    }




    // 定义一个挂起函数 `run`，用于处理网络请求和响应
    private suspend fun run() {
        // 创建一个Protobuf POST请求，指定请求的URL和请求体
        val request = ProtobufPostRequest(
            // URL包含API地址和必要的查询参数
            "https://datamixer-pa.googleapis.com/v1/mutateonekey?alt=proto&key=AIzaSyAP-gfH3qvi6vgHZbSYwQ_XHqV_mXHhzIk",
            MutateOperation(
                id = MutateOperationId.AppInviteLink, // 操作类型为 AppInviteLink
                mutateRequest = MutateDataRequest(
                    appInviteLink = MutateAppInviteLinkRequest(
                        client = ClientIdInfo(
                            packageName = Constants.GMS_PACKAGE_NAME, // 设置包名常量
                            signature = Constants.GMS_PACKAGE_SIGNATURE_SHA1.decodeHex()
                                .base64(), // 解码并编码签名
                            language = Locale.getDefault().language // 设置当前默认语言
                        ),
                        link = LinkInfo(
                            invitationId = "", // 邀请ID（空字符串表示未指定）
                            uri = intent.data.toString() // 从Intent获取数据并转换为字符串
                        ),
                        system = SystemInfo(
                            gms = SystemInfo.GmsInfo(
                                versionCode = Constants.GMS_VERSION_CODE // 设置GMS版本代码常量
                            )
                        )
                    )
                )
            ), MutateDataResponseWithError.ADAPTER // 设置适配器以处理响应
        )
        // 发送请求并等待响应，处理可能的异常
        val response = try {
            request.sendAndAwait(queue) // 发送请求并等待响应
        } catch (e: Exception) {
            Log.w(TAG, e) // 记录异常
            return redirectToBrowser() // 异常时重定向到浏览器
        }
        // 根据响应状态决定后续操作
        if (response.errorStatus != null || response.dataResponse?.appInviteLink == null)
            return redirectToBrowser() // 如果有错误或无有效链接则重定向到浏览器
        open(response.dataResponse.appInviteLink) // 打开链接
    }

    private suspend fun run2() {
        val url = "https://firebasedynamiclinks.googleapis.com/v1/getLinkResolution?key=AIzaSyAP-gfH3qvi6vgHZbSYwQ_XHqV_mXHhzIk"

        // 创建 JSON 对象作为请求体
        val jsonRequestBody = JSONObject().apply {
            put("android_package_name", "com.google.android.gms")
            put("checksum", "2T0/hfuHFzgHLBkZA2EdFetp7YuPjvIMo/uVFGBdADI=:20231204")
            put("device_language", "中文")
            put("os_version", "13")
            put("requested_link", "https://search.app.goo.gl/?ofl=https%3A%2F%2Flens.google&al=googleapp%3A%2F%2Flens%3Flens_data%3DKAw&apn=com.google.android.googlequicksearchbox&amv=301204913&isi=284815942&ius=googleapp&ibi=com.google.GoogleMobile&link=https%3A%2F%2Fgoo.gl%2Fiosgoogleapp%2Fdefault%3Furl%3Dgoogleapp%253A%252F%252Flens%253Fmin-version%253D180%2526lens_data%253DKAw&ifl=https%3A%2F%2Fapps.apple.com%2Fus%2Fapp%2Fgoogle%2Fid284815942%3Fppid%3D1ac8cc35-d99c-4a1d-b909-321c8968cc74%26pt%3D9008%26mt%3D8%26ct%3D4815459-oo-lens-isb-bar-lens-cam%26UTM_campaign%3Dgoogle_search_mweb&efr=1&ct=4815459-oo-lens-isb-bar-lens-cam&utm_campaign=4815459-oo-lens-isb-bar-lens-cam&utm_source=google_search_mweb&utm_medium=owned&pt=9008&mt=8")
            put("sdk_version", "24.16.16 (190400-{{cl}}):appinvite")
        }

        val request = JsonPostRequest(url, jsonRequestBody)

        // 发送请求并等待响应，处理可能的异常
        val response = try {
            request.sendAndAwait(queue) // 发送请求并等待响应
        } catch (e: Exception) {
            Log.w(TAG, e) // 记录异常
            return redirectToBrowser() // 异常时重定向到浏览器
        }

        // 根据响应状态决定后续操作
        if (response.has("error")) {
            Log.w(TAG, "Error in response: ${response.optString("error")}")
            return redirectToBrowser() // 如果有错误或无有效链接则重定向到浏览器
        }

        openDynamicLink(response)
    }
}

class JsonPostRequest(
    url: String,
    private val jsonRequestBody: JSONObject
) : Request<JSONObject>(Method.POST, url, null) {
    private val deferred = CompletableDeferred<JSONObject>()

    override fun getHeaders(): Map<String, String> {
        val headers = HashMap(super.getHeaders())
        headers["Accept-Language"] = if (SDK_INT >= 24) LocaleList.getDefault().toLanguageTags() else Locale.getDefault().language
        headers["X-Android-Package"] = Constants.GMS_PACKAGE_NAME
        headers["X-Android-Cert"] = Constants.GMS_PACKAGE_SIGNATURE_SHA1
        headers["Content-Type"] = "application/json"
        return headers
    }

    override fun getBody(): ByteArray = jsonRequestBody.toString().toByteArray(Charsets.UTF_8)

    override fun getBodyContentType(): String = "application/json"

    override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
        return try {
            Response.success(JSONObject(String(response.data, Charsets.UTF_8)), null)
        } catch (e: Exception) {
            Response.error(VolleyError(e))
        }
    }

    override fun deliverResponse(response: JSONObject) {
        Log.d(TAG, "Got response: $response")
        deferred.complete(response)
    }

    override fun deliverError(error: VolleyError) {
        Log.e(TAG, "Failed to get response: $error")
        deferred.completeExceptionally(error)
    }

    suspend fun await(): JSONObject = deferred.await()

    suspend fun sendAndAwait(queue: RequestQueue): JSONObject {
        queue.add(this)
        return await()
    }
}

data class AppInviteLinkData(
    val deepLink: String,
    val targetAndroidPackage: String,
    val minAndroidAppVersionCode: Int,
    val fallbackUri: String,
    val resolvedLink: String
)


class ProtobufPostRequest<I : Message<I, *>, O>(
    url: String,
    private val i: I,
    private val oAdapter: ProtoAdapter<O>
) :
    Request<O>(Method.POST, url, null) {
    private val deferred = CompletableDeferred<O>()

    override fun getHeaders(): Map<String, String> {
        val headers = HashMap(super.getHeaders())
        headers["Accept-Language"] = if (SDK_INT >= 24) LocaleList.getDefault()
            .toLanguageTags() else Locale.getDefault().language
        headers["X-Android-Package"] = Constants.GMS_PACKAGE_NAME
        headers["X-Android-Cert"] = Constants.GMS_PACKAGE_SIGNATURE_SHA1
        return headers
    }

    override fun getBody(): ByteArray = i.encode()

    override fun getBodyContentType(): String = "application/x-protobuf"

    override fun parseNetworkResponse(response: NetworkResponse): Response<O> {
        try {
            return Response.success(oAdapter.decode(response.data), null)
        } catch (e: VolleyError) {
            return Response.error(e)
        } catch (e: Exception) {
            return Response.error(VolleyError())
        }
    }

    override fun deliverResponse(response: O) {
        Log.d(TAG, "Got response: $response")
        deferred.complete(response)
    }

    override fun deliverError(error: VolleyError) {
        deferred.completeExceptionally(error)
    }

    suspend fun await(): O = deferred.await()

    suspend fun sendAndAwait(queue: RequestQueue): O {
        Log.d(TAG, "Sending request: $i")
        queue.add(this)
        return await()
    }
}