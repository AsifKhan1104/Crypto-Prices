package com.crypto.prices.model

import java.math.BigDecimal

data class DerivativesDetailData(
    val basis: BigDecimal,
    val contract_type: String,
    //val expired_at: Any,
    val funding_rate: BigDecimal,
    val index: BigDecimal,
    val index_id: String,
    val last_traded_at: Long,
    val market: String,
    val open_interest: BigDecimal,
    val price: String,
    val price_percentage_change_24h: BigDecimal,
    val spread: BigDecimal,
    val symbol: String,
    val volume_24h: BigDecimal
)