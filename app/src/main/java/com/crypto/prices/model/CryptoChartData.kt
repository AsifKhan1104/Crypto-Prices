package com.crypto.prices.model

import java.math.BigDecimal

data class CryptoChartData(
    val market_caps: List<List<BigDecimal?>>,
    val prices: List<List<BigDecimal?>>,
    val total_volumes: List<List<BigDecimal?>>
)