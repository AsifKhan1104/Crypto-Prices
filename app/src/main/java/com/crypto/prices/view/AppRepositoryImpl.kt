package com.crypto.prices.view

import com.crypto.prices.model.CryptoDataa
import com.crypto.prices.model.NewsData
import com.crypto.prices.model.Trending
import com.crypto.prices.remote.Service
import retrofit2.Response

class AppRepositoryImpl : AppRepository {
    val service = Service().getCGService()
    val serviceCM = Service().getCMService()
    val serviceNews = Service().getNewsService()

    //override suspend fun getCryptoPrices(): Response<ListingsLatest> = serviceCM.getListingsLatest()
    override suspend fun getCryptoPrices(map: MutableMap<String, String>): Response<List<CryptoDataa>> = service.getCryptoCoins(map)
    override suspend fun getTrendingCoins(): Response<Trending> = service.getTrendingCoins()
    override suspend fun getAllNews(map: MutableMap<String, String>): Response<NewsData> = serviceNews.getAllNews(map)

}