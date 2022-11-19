package com.crypto.prices.model.exchangeRates

data class Dot(
    val name: String,
    val type: String,
    val unit: String,
    val value: Double
)