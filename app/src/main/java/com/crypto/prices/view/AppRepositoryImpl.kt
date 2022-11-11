package com.crypto.prices.view

import com.crypto.prices.model.ListingsLatest
import com.crypto.prices.model.Trending
import com.crypto.prices.remote.Service
import retrofit2.Response

class AppRepositoryImpl : AppRepository {
    val service = Service().getCGService()
    val serviceCM = Service().getCMService()

    override suspend fun getCryptoPrices(): Response<ListingsLatest> = serviceCM.getListingsLatest()
    override suspend fun getTrendingCoins(): Response<Trending> = service.getTrendingCoins()
}