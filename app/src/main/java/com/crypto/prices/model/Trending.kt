package com.crypto.prices.model

data class Trending(
    val coins: List<CoinX>,
    val exchanges: List<Any>
)

data class CoinX(
    val item: Item
)

data class Item(
    val coin_id: Int,
    val id: String,
    val large: String,
    val market_cap_rank: Int,
    val name: String,
    val price_btc: Double,
    val score: Int,
    val slug: String,
    val small: String,
    val symbol: String,
    val thumb: String
)