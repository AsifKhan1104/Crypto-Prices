package com.crypto.prices.view

import com.crypto.prices.model.ListingsLatest
import retrofit2.Response

interface AppRepository {
    suspend fun getCryptoPrices(): Response<ListingsLatest>
}