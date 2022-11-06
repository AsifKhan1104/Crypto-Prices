package com.crypto.prices.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ListingsLatest(
    val data: List<Data>,
    val status: Status
): Parcelable

@Parcelize
data class Data(
    val circulating_supply: Double,
    val cmc_rank: Int,
    val date_added: String,
    val id: Int,
    val last_updated: String,
    val max_supply: Double,
    val name: String,
    val num_market_pairs: Int,
    /*val platform: Any,*/
    val quote: Quote,
    val slug: String,
    val symbol: String,
    val tags: List<String>,
    val total_supply: Double
) : Parcelable

@Parcelize
data class Status(
    val credit_count: Int,
    val elapsed: Int,
    val error_code: Int,
    val error_message: String,
    /*val notice: Any,*/
    val timestamp: String,
    val total_count: Int
) : Parcelable

@Parcelize
data class Quote(
    val USD: USD
) : Parcelable

@Parcelize
data class USD(
    val fully_diluted_market_cap: Double,
    val last_updated: String,
    val market_cap: Double,
    val market_cap_dominance: Double,
    val percent_change_1h: Double,
    val percent_change_24h: Double,
    val percent_change_30d: Double,
    val percent_change_60d: Double,
    val percent_change_7d: Double,
    val percent_change_90d: Double,
    val price: Double,
    val volume_24h: Double,
    val volume_change_24h: Double
) : Parcelable