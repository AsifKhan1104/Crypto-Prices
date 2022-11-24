package com.crypto.prices.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class ExchangesData(
    val country: String?,
    val description: String?,
    val has_trading_incentive: Boolean?,
    val id: String?,
    val image: String?,
    val name: String?,
    val trade_volume_24h_btc: BigDecimal,
    val trade_volume_24h_btc_normalized: BigDecimal,
    val trust_score: Int?,
    val trust_score_rank: Int?,
    val url: String?,
    val year_established: Int?
) : Parcelable