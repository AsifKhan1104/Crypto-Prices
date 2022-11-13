package com.crypto.prices.remote

import com.crypto.prices.model.CryptoDataa
import com.crypto.prices.model.ListingsLatest
import com.crypto.prices.model.NewsData
import com.crypto.prices.model.Trending
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.QueryMap

interface Api {
    @Headers("X-CMC_PRO_API_KEY: 5830540b-c91b-4428-8c1f-08b7073aa1b8")
    @GET("v1/cryptocurrency/listings/latest")
    suspend fun getListingsLatest(): Response<ListingsLatest>

    @GET("v3/coins/markets")
    suspend fun getCryptoCoins(@QueryMap map: Map<String, Any>): Response<List<CryptoDataa>>

    @GET("v3/search/trending")
    suspend fun getTrendingCoins(): Response<Trending>

    @GET("v2/everything")
    suspend fun getAllNews(@QueryMap map: Map<String, String>): Response<NewsData>
}