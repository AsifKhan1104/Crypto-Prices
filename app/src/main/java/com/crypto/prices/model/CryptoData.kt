package com.crypto.prices.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class CryptoData(
    val ath: BigDecimal?,
    val ath_change_percentage: BigDecimal?,
    val ath_date: String?,
    val atl: BigDecimal?,
    val atl_change_percentage: BigDecimal?,
    val atl_date: String?,
    val circulating_supply: BigDecimal?,
    val current_price: BigDecimal?,
    val fully_diluted_valuation: BigDecimal?,
    val high_24h: BigDecimal?,
    val id: String?,
    val image: String?,
    val last_updated: String?,
    val low_24h: BigDecimal?,
    val market_cap: BigDecimal?,
    val market_cap_change_24h: BigDecimal?,
    val market_cap_change_percentage_24h: BigDecimal?,
    val market_cap_rank: Int,
    val max_supply: BigDecimal?,
    val name: String?,
    val price_change_24h: BigDecimal?,
    val price_change_percentage_24h: BigDecimal?,
    //val roi: Roi,
    val symbol: String?,
    val total_supply: BigDecimal?,
    val total_volume: BigDecimal?
) : Parcelable

data class Roi(
    val currency: String?,
    val percentage: BigDecimal?,
    val times: BigDecimal?
)