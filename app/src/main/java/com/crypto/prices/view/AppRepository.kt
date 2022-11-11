package com.crypto.prices.view

import com.crypto.prices.model.ListingsLatest
import com.crypto.prices.model.Trending
import retrofit2.Response

interface AppRepository {
    suspend fun getCryptoPrices(): Response<ListingsLatest>
    suspend fun getTrendingCoins(): Response<Trending>
}