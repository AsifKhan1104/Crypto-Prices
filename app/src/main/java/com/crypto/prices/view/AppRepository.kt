package com.crypto.prices.view

import com.crypto.prices.model.CryptoDataa
import com.crypto.prices.model.NewsData
import com.crypto.prices.model.Trending
import retrofit2.Response

interface AppRepository {
    //suspend fun getCryptoPrices(): Response<ListingsLatest>
    suspend fun getCryptoPrices(map: MutableMap<String, Any>): Response<List<CryptoDataa>>
    suspend fun getTrendingCoins(): Response<Trending>
    suspend fun getAllNews(map: MutableMap<String, String>): Response<NewsData>
}