package com.crypto.prices.data.online

import com.crypto.prices.model.*
import com.crypto.prices.model.crypto.search.CryptoDetailData
import com.crypto.prices.model.exchangeRates.ExchangeRates
import com.crypto.prices.remote.Api
import com.crypto.prices.remote.NewsApi
import com.crypto.prices.remote.PriceConversionApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val service: Api,
    private val serviceNews: NewsApi,
    private val servicePriceConversion: PriceConversionApi
) : AppRepository {

    //override suspend fun getCryptoPrices(): Response<ListingsLatest> = serviceCM.getListingsLatest()
    override suspend fun getCryptoPrices(map: MutableMap<String, String>): Response<List<CryptoData>> =
        service.getCryptoCoins(map)

    override suspend fun getCryptoPricesChart(
        id: String,
        map: MutableMap<String, String>
    ): Response<CryptoChartData> =
        service.getCryptoChart(id, map)

    override suspend fun getCryptoCoinDetail(
        id: String,
        map: MutableMap<String, String>
    ): Response<CryptoDetailData> = service.getCryptoCoinDetail(id, map)

    override suspend fun getCategories(map: MutableMap<String, String>): Response<List<CategoriesData>> =
        service.getCategories(map)

    override suspend fun getExchanges(map: MutableMap<String, String>): Response<List<ExchangesData>> =
        service.getExchanges(map)

    override suspend fun getExchangesChart(
        id: String,
        map: MutableMap<String, String>
    ) = service.getExchangesChart(id, map)

    override suspend fun getExchange(id: String): Response<ExchangeDataSearch> =
        service.getExchange(id)

    override suspend fun getDerivatives(map: MutableMap<String, String>): Response<List<DerivativesData>> =
        service.getDerivatives(map)

    override suspend fun getDerivativesDetail(map: MutableMap<String, String>): Response<List<DerivativesDetailData>> =
        service.getDerivativesDetail(map)

    override suspend fun getNfts(map: MutableMap<String, String>): Response<List<NftData>> =
        service.getNfts(map)

    override suspend fun getNftData(id: String): Response<NftDetailData> = service.getNftData(id)

    override suspend fun getTrendingCoins(): Response<Trending> = service.getTrendingCoins()
    override suspend fun getExchangeRates(): Response<ExchangeRates> = service.getExchangeRates()
    override suspend fun getSupportedCurrList(): Response<List<String>> =
        service.getSupportedCurrency()

    override suspend fun getPriceConversion(map: MutableMap<String, String>): Response<String> =
        servicePriceConversion.getPriceConversion(map)

    override suspend fun getSearchResults(query: String): Flow<Response<SearchData>> =
        flow { emit(service.getSearchResults(query)) }

    override suspend fun getAllNews(map: MutableMap<String, String>): Response<NewsData> =
        serviceNews.getAllNews(map)

}