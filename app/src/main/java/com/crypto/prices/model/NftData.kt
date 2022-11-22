package com.crypto.prices.model

data class NftData(
    val asset_platform_id: String,
    val contract_address: String,
    val id: String,
    val name: String,
    val symbol: String
)