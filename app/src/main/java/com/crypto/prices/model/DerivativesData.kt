package com.crypto.prices.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class DerivativesData(
    //val country: Any,
    val description: String,
    val id: String,
    val image: String,
    val name: String,
    val number_of_futures_pairs: Int,
    val number_of_perpetual_pairs: Int,
    val open_interest_btc: BigDecimal,
    val trade_volume_24h_btc: String,
    val url: String,
    val year_established: Int
) : Parcelable