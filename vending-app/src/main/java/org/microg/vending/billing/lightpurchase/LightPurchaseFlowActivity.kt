package org.microg.vending.billing.lightpurchase

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import org.microg.vending.billing.ContextProvider
import org.microg.vending.billing.KEY_IAP_SHEET_UI_PARAM
import org.microg.vending.billing.InAppBillingServiceImpl

class LightPurchaseFlowActivity : Activity() {

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inAppBillingService = InAppBillingServiceImpl(this)
        Log.d("InAppBillingService", "InAppBillingService OK!")

        intent?.extras?.let { extras ->
            for (key in extras.keySet()) {
                val value = extras.get(key)
                Log.d("LightPurchaseIntent", "Key: $key Value: $value")
            }
        }

        val authAccount = intent.getStringExtra("authAccount")
        Log.d("InAppBillingService", authAccount.toString())
        val apiVersion = 17
        val packageName = "com.google.android.videos"
        val sku = intent.getStringExtra("full_docid").toString()
        val type = ""
        val developerPayload: String? = null
        val extraParams = Bundle().apply {
            putStringArrayList("skuDetailsTokens", arrayListOf("AEuhp4J2effVIRXrzu_DeYrokQOHkmxtI6YO7pWM15K6fGt7udDJwO0wf9BsIrk5nG3vlvWzM94nA-o="))
            putString("proxyPackageVersion", "package not found")
            putStringArrayList("SKU_OFFER_ID_TOKEN_LIST", arrayListOf("AbNbjn6qd8NV87Re/Gu8BaDG15I1ttYTPGQklCYCEKswhA2AYrbJgEaLxvr8PzAhsQYGGMuXkxbh/8kb2wySL7bDGg=="))
            putString("playBillingLibraryVersion", "6.1.0")
            putBoolean("enablePendingPurchases", true)
            putString("proxyPackage", "io.flutter.plugins.inapppurchase")
        }

        try {
            val result = inAppBillingService.getBuyIntentExtraParams(
                    apiVersion,
                    packageName,
                    sku,
                    type,
                    developerPayload,
                    extraParams
            )
            Log.d("LightPurchase", "Result: $result")
        } catch (e: Exception) {
            Log.e("LightPurchase", "Error calling getBuyIntentExtraParams", e)
        }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)
    }
}