package com.crypto.prices.model

data class WithdrawReq(
    val uid: String? = null,
    val name: String? = null,
    val walletAddress: String? = null,
    val currentDataTime: String? = null,
    val currentBal: Double? = 0.000000,
    val withdrawCoins: Double? = 0.000000,
    val status: String? = null,
    val remark: String? = null,
    val isPro: Boolean? = null
) {

}