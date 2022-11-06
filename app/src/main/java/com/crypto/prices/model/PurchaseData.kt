package com.crypto.prices.model

import com.android.billingclient.api.Purchase

data class PurchaseData(
    val uid: String? = null,
    val isPro: Boolean? = null,
    val purchaseDetails: Purchase? = null
)