package com.crypto.prices.remote

import com.crypto.prices.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface Api {
    @Headers("X-CMC_PRO_API_KEY: 5830540b-c91b-4428-8c1f-08b7073aa1b8")
    @GET("v1/cryptocurrency/listings/latest")
    suspend fun getListingsLatest(): Response<ListingsLatest>

    @GET("v3/coins/markets")
    suspend fun getCryptoCoins(@QueryMap map: Map<String, String>): Response<List<CryptoData>>

    @GET("v3/coins/{id}/market_chart")
    suspend fun getCryptoChart(
        @Path("id") id: String,
        @QueryMap map: Map<String, String>
    ): Response<CryptoChartData>

    @GET("v3/coins/categories")
    suspend fun getCategories(@QueryMap map: Map<String, String>): Response<List<CategoriesData>>

    @GET("v3/search/trending")
    suspend fun getTrendingCoins(): Response<Trending>

    @GET("v2/everything")
    suspend fun getAllNews(@QueryMap map: Map<String, String>): Response<NewsData>
}