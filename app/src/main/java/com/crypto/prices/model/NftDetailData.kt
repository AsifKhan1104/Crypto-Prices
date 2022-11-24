package com.crypto.prices.model

import java.math.BigDecimal

data class NftDetailData(
    val asset_platform_id: String,
    val contract_address: String,
    val description: String,
    val floor_price: FloorPrice,
    val floor_price_in_usd_24h_percentage_change: BigDecimal,
    val id: String,
    val image: Image,
    val market_cap: MarketCap,
    val name: String,
    val native_currency: String,
    val number_of_unique_addresses: Int,
    val number_of_unique_addresses_24h_percentage_change: BigDecimal,
    val total_supply: Int,
    val volume_24h: Volume24h
)

data class FloorPrice(
    val native_currency: BigDecimal,
    val usd: BigDecimal
)

data class Image(
    val small: String
)

data class MarketCap(
    val native_currency: BigDecimal,
    val usd: BigDecimal
)

data class Volume24h(
    val native_currency: BigDecimal,
    val usd: BigDecimal
)