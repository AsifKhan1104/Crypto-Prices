package com.crypto.prices.model

data class SearchData(
    val categories: List<Category>,
    val coins: List<Coin>,
    val exchanges: List<Exchange>,
    val icos: List<Any>,
    val nfts: List<Nft>
)

data class Category(
    val id: Int,
    val name: String
)

data class Coin(
    val api_symbol: String,
    val id: String,
    val large: String,
    val market_cap_rank: Int,
    val name: String,
    val symbol: String,
    val thumb: String
)

data class Exchange(
    val id: String,
    val large: String,
    val market_type: String,
    val name: String,
    val thumb: String
)

data class Nft(
    val id: String,
    val name: String,
    val symbol: String,
    val thumb: String
)