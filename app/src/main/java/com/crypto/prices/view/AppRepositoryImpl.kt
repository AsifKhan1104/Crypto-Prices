package com.crypto.prices.view

import com.crypto.prices.model.ListingsLatest
import com.crypto.prices.remote.Service
import retrofit2.Response

class AppRepositoryImpl : AppRepository {
    val service = Service().getUsersService()

    override suspend fun getCryptoPrices(): Response<ListingsLatest> = service.getListingsLatest()
}