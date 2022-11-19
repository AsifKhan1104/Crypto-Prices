package com.crypto.prices.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class CategoriesData(
    val content: String,
    val id: String,
    val market_cap: BigDecimal,
    val market_cap_change_24h: BigDecimal,
    val name: String,
    val top_3_coins: List<String>,
    val updated_at: String,
    val volume_24h: BigDecimal
) : Parcelable