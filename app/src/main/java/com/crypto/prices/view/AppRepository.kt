package com.crypto.prices.view

import com.crypto.prices.model.*
import com.crypto.prices.model.crypto.search.CryptoDetailData
import com.crypto.prices.model.exchangeRates.ExchangeRates
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.math.BigDecimal

interface AppRepository {
    //suspend fun getCryptoPrices(): Response<ListingsLatest>
    suspend fun getCryptoPrices(map: MutableMap<String, String>): Response<List<CryptoData>>
    suspend fun getCryptoPricesChart(
        id: String,
        map: MutableMap<String, String>
    ): Response<CryptoChartData>
    suspend fun getCryptoCoinDetail(
        id: String,
        map: MutableMap<String, String>
    ): Response<CryptoDetailData>

    suspend fun getCategories(map: MutableMap<String, String>): Response<List<CategoriesData>>
    suspend fun getExchanges(map: MutableMap<String, String>): Response<List<ExchangesData>>
    suspend fun getExchangesChart(
        id: String,
        map: MutableMap<String, String>
    ): Response<ArrayList<ArrayList<BigDecimal>>>
    suspend fun getExchange(id: String): Response<ExchangeDataSearch>

    suspend fun getNfts(map: MutableMap<String, String>): Response<List<NftData>>
    suspend fun getNftData(id: String): Response<NftDetailData>
    suspend fun getTrendingCoins(): Response<Trending>
    suspend fun getExchangeRates(): Response<ExchangeRates>
    suspend fun getSearchResults(query: String): Flow<Response<SearchData>>
    suspend fun getAllNews(map: MutableMap<String, String>): Response<NewsData>
}