package com.crypto.prices.model

import java.math.BigDecimal

data class ExchangeDataSearch(
    val alert_notice: String,
    val centralized: Boolean,
    val country: String,
    val description: String,
    val facebook_url: String,
    val has_trading_incentive: Boolean,
    val image: String,
    val name: String,
    val other_url_1: String,
    val other_url_2: String,
    val public_notice: String,
    val reddit_url: String,
    val slack_url: String,
    val status_updates: List<StatusUpdate>,
    val telegram_url: String,
    val tickers: List<Ticker>,
    val trade_volume_24h_btc: BigDecimal,
    val trade_volume_24h_btc_normalized: BigDecimal,
    val trust_score: Int,
    val trust_score_rank: Int,
    val twitter_handle: String,
    val url: String,
    val year_established: Int
)

data class StatusUpdate(
    val category: String,
    val created_at: String,
    val description: String,
    val pin: Boolean,
    val project: Project,
    val user: String,
    val user_title: String
)

data class Ticker(
    val base: String,
    val bid_ask_spread_percentage: BigDecimal,
    val coin_id: String,
    val converted_last: ConvertedLast,
    val converted_volume: ConvertedVolume,
    val is_anomaly: Boolean,
    val is_stale: Boolean,
    val last: BigDecimal,
    val last_fetch_at: String,
    val last_traded_at: String,
    val market: Market,
    val target: String,
    val target_coin_id: String,
    val timestamp: String,
    val token_info_url: Any,
    val trade_url: String,
    val trust_score: String,
    val volume: BigDecimal
)

data class ConvertedLast(
    val btc: BigDecimal,
    val eth: BigDecimal,
    val usd: BigDecimal
)

data class ConvertedVolume(
    val btc: BigDecimal,
    val eth: BigDecimal,
    val usd: BigDecimal
)

data class Project(
    val id: String,
    val image: Image,
    val name: String,
    val type: String
) {
    data class Image(
        val large: String,
        val small: String,
        val thumb: String
    )
}

data class Market(
    val has_trading_incentive: Boolean,
    val identifier: String,
    val name: String
)