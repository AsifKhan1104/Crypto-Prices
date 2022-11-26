package com.crypto.prices.model.crypto.search

import java.math.BigDecimal

data class CryptoDetailData(
    val additional_notices: List<Any>,
    val asset_platform_id: Any,
    val block_time_in_minutes: Int,
    val categories: List<String>,
    val coingecko_rank: Int,
    val coingecko_score: BigDecimal,
    val community_score: BigDecimal,
    val country_origin: String,
    val description: Description,
    // val detail_platforms: DetailPlatforms,
    val developer_score: BigDecimal,
    val genesis_date: Any,
    val hashing_algorithm: String,
    val id: String,
    val image: Image,
    val last_updated: String,
    val links: Links,
    val liquidity_score: BigDecimal,
    val market_cap_rank: BigDecimal,
    val market_data: MarketData,
    val name: String,
    // val platforms: Platforms,
    val public_interest_score: BigDecimal,
    val public_interest_stats: PublicInterestStats,
    val public_notice: Any,
    val sentiment_votes_down_percentage: Any,
    val sentiment_votes_up_percentage: Any,
    val status_updates: List<Any>,
    val symbol: String
) {
    data class Description(
        val en: String
    )

    /*data class DetailPlatforms(
        val : X
    )*/

    data class Image(
        val large: String,
        val small: String,
        val thumb: String
    )

    data class MarketData(
        val ath: Ath,
        val ath_change_percentage: AthChangePercentage,
        val ath_date: AthDate,
        val atl: Atl,
        val atl_change_percentage: AtlChangePercentage,
        val atl_date: AtlDate,
        val circulating_supply: BigDecimal,
        val current_price: CurrentPrice,
        val fdv_to_tvl_ratio: Any,
        val fully_diluted_valuation: FullyDilutedValuation,
        val high_24h: High24h,
        val last_updated: String,
        val low_24h: Low24h,
        val market_cap: MarketCap,
        val market_cap_change_24h: BigDecimal,
        val market_cap_change_24h_in_currency: MarketCapChange24hInCurrency,
        val market_cap_change_percentage_24h: BigDecimal,
        val market_cap_change_percentage_24h_in_currency: MarketCapChangePercentage24hInCurrency,
        val market_cap_rank: Int,
        val max_supply: BigDecimal,
        val mcap_to_tvl_ratio: Any,
        val price_change_24h: BigDecimal,
        val price_change_24h_in_currency: PriceChange24hInCurrency,
        val price_change_percentage_14d: BigDecimal,
        val price_change_percentage_14d_in_currency: PriceChangePercentage14dInCurrency,
        val price_change_percentage_1h_in_currency: PriceChangePercentage1hInCurrency,
        val price_change_percentage_1y: BigDecimal,
        val price_change_percentage_1y_in_currency: PriceChangePercentage1yInCurrency,
        val price_change_percentage_200d: BigDecimal,
        val price_change_percentage_200d_in_currency: PriceChangePercentage200dInCurrency,
        val price_change_percentage_24h: BigDecimal,
        val price_change_percentage_24h_in_currency: PriceChangePercentage24hInCurrency,
        val price_change_percentage_30d: BigDecimal,
        val price_change_percentage_30d_in_currency: PriceChangePercentage30dInCurrency,
        val price_change_percentage_60d: BigDecimal,
        val price_change_percentage_60d_in_currency: PriceChangePercentage60dInCurrency,
        val price_change_percentage_7d: BigDecimal,
        val price_change_percentage_7d_in_currency: PriceChangePercentage7dInCurrency,
        val roi: Any,
        val total_supply: BigDecimal,
        val total_value_locked: Any,
        val total_volume: TotalVolume
    )

    data class Links(
        val announcement_url: List<String>,
        val bitcointalk_thread_identifier: Int,
        val blockchain_site: List<String>,
        val chat_url: List<String>,
        val facebook_username: String,
        val homepage: List<String>,
        val official_forum_url: List<String>,
        val repos_url: ReposUrl,
        val subreddit_url: String,
        val telegram_channel_identifier: String,
        val twitter_screen_name: String
    ) {
        data class ReposUrl(
            val bitbucket: List<Any>,
            val github: List<String>
        )
    }

    /*data class Platforms(
        val : String
    )*/

    data class PublicInterestStats(
        val alexa_rank: Int,
        val bing_matches: Any
    )
}