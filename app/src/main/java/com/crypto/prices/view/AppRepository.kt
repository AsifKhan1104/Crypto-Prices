package com.crypto.prices.view

import com.crypto.prices.model.*
import com.crypto.prices.model.exchangeRates.ExchangeRates
import retrofit2.Response

interface AppRepository {
    //suspend fun getCryptoPrices(): Response<ListingsLatest>
    suspend fun getCryptoPrices(map: MutableMap<String, String>): Response<List<CryptoData>>
    suspend fun getCryptoPricesChart(
        id: String,
        map: MutableMap<String, String>
    ): Response<CryptoChartData>

    suspend fun getCategories(map: MutableMap<String, String>): Response<List<CategoriesData>>
    suspend fun getExchanges(map: MutableMap<String, String>): Response<List<ExchangesData>>
    suspend fun getNfts(map: MutableMap<String, String>): Response<List<NftData>>
    suspend fun getNftData(id: String): Response<NftDetailData>
    suspend fun getTrendingCoins(): Response<Trending>
    suspend fun getExchangeRates(): Response<ExchangeRates>
    suspend fun getAllNews(map: MutableMap<String, String>): Response<NewsData>
}