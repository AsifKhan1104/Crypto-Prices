package com.crypto.prices.remote

import com.crypto.prices.model.*
import com.crypto.prices.model.exchangeRates.ExchangeRates
import retrofit2.Response
import retrofit2.http.*
import java.math.BigDecimal

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

    @GET("v3/exchanges")
    suspend fun getExchanges(@QueryMap map: Map<String, String>): Response<List<ExchangesData>>

    @GET("v3/exchanges/{id}/volume_chart")
    suspend fun getExchangesChart(
        @Path("id") id: String,
        @QueryMap map: Map<String, String>
    ): Response<ArrayList<ArrayList<BigDecimal>>>

    @GET("v3/nfts/list")
    suspend fun getNfts(@QueryMap map: Map<String, String>): Response<List<NftData>>

    @GET("v3/nfts/{id}")
    suspend fun getNftData(@Path("id") id: String): Response<NftDetailData>

    @GET("v3/search/trending")
    suspend fun getTrendingCoins(): Response<Trending>

    @GET("v3/exchange_rates")
    suspend fun getExchangeRates(): Response<ExchangeRates>

    @GET("v3/search")
    suspend fun getSearchResults(@Query("query") query: String): Response<SearchData>

    @GET("v2/everything")
    suspend fun getAllNews(@QueryMap map: Map<String, String>): Response<NewsData>
}