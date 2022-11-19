package com.crypto.prices.view

import com.crypto.prices.model.*
import retrofit2.Response

interface AppRepository {
    //suspend fun getCryptoPrices(): Response<ListingsLatest>
    suspend fun getCryptoPrices(map: MutableMap<String, String>): Response<List<CryptoData>>
    suspend fun getCryptoPricesChart(
        id: String,
        map: MutableMap<String, String>
    ): Response<CryptoChartData>

    suspend fun getCategories(): Response<List<CategoriesData>>
    suspend fun getTrendingCoins(): Response<Trending>
    suspend fun getAllNews(map: MutableMap<String, String>): Response<NewsData>
}