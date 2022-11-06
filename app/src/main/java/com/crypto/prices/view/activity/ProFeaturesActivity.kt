package com.crypto.prices.view.activity

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*
import com.android.billingclient.api.Purchase.PurchaseState.PURCHASED
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.crypto.prices.R
import com.crypto.prices.model.PurchaseData
import com.crypto.prices.utils.MySharedPrefs
import com.crypto.prices.utils.TableManagement
import com.crypto.prices.utils.Utility
import kotlinx.android.synthetic.main.activity_pro_features.*

class ProFeaturesActivity : AppCompatActivity() {
    lateinit var skuDetails: SkuDetails
    lateinit var billingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pro_features)

        billingClient = BillingClient.newBuilder(this)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()

        listenPendingPurchases()
        startBillingFlow()

        // click on billing flow
        try {
            buttonStartBilling.setOnClickListener(View.OnClickListener {
                if (skuDetails != null) {
                    val flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build()
                    val responseCode =
                        billingClient.launchBillingFlow(this, flowParams).responseCode
                } else {
                    Utility.showToast(
                        this,
                        "Oops!! Some details are loading. Please try again in 5 mins"
                    )
                }
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    // start connection with google play
    private fun startBillingFlow() {
        try {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // The BillingClient is ready. You can query purchases here.
                        querySkuDetails()
                    }
                }

                override fun onBillingServiceDisconnected() {
                    // Try to restart the connection on the next request to
                    // Google Play by calling the startConnection() method.
                    startBillingFlow()
                }
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    handleNonConsumablePurchase(purchase)
                }
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                Utility.showToast(this, getString(R.string.billing_cancelled))
            } else {
                // Handle any other error codes.
                Utility.showToast(this, getString(R.string.error_billing))
            }
        }

    fun querySkuDetails() {
        val skuList = ArrayList<String>()
        skuList.add("1947_pro_feature")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        billingClient.querySkuDetailsAsync(
            params.build(), { billingResult, skuList ->
                if (!skuList.isNullOrEmpty()) {
                    skuDetails = skuList?.get(0)
                }
            })
    }

    private fun handleNonConsumablePurchase(purchase: Purchase) {
        Log.d("TAG_INAPP", "handlePurchase : ${purchase}")
        if (purchase.purchaseState == PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken).build()
                billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    val billingResponseCode = billingResult.responseCode
                    val billingDebugMessage = billingResult.debugMessage

                    // save pro users in firebase db
                    val user = Firebase.auth.currentUser
                    // adding to pro list for quick check of pro users
                    Firebase.database.reference.child(TableManagement.PRO_USERS_LIST).child(user!!.uid).setValue(true)
                    val purchaseData = PurchaseData(user!!.uid, true, purchase)
                    Firebase.database.reference.child(TableManagement.PRO_USERS_DATA).child(user!!.uid)
                        .setValue(purchaseData)
                    // save isPro in sharedPrefs also
                    MySharedPrefs.getInstance(this).saveBoolean(getString(R.string.isPro), true)

                    // show success dialog
                    showDialogPro(
                        this,
                        getString(R.string.congrats_title),
                        getString(R.string.congrats_msg_user_pro)
                    )
                }
            }
        }
    }

    fun showDialogPro(context: Context?, title: String?, message: String?) {
        val builder = AlertDialog.Builder(context)
        builder.setPositiveButton("ok") { dialog, _ ->
            dialog.dismiss()
            this.finish()
        }
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
    }

    private fun listenPendingPurchases() {
        try {
            billingClient.queryPurchasesAsync(
                BillingClient.SkuType.INAPP,
                PurchasesResponseListener { billingResult, purchases ->
                    // To be implemented in a later section.
                    if (purchases.size > 0) {
                        for (purchase in purchases) {
                            handleNonConsumablePurchase(purchase)
                        }
                    }
                })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}