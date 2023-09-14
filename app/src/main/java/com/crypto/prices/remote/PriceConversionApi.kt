package com.crypto.prices.remote

import com.crypto.prices.model.NewsData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.QueryMap

/**
 * Created by Asif Khan on 13/09/23.
 */
interface PriceConversionApi {
    @Headers("X-CMC_PRO_API_KEY: 5830540b-c91b-4428-8c1f-08b7073aa1b8")
    @GET("v2/tools/price-conversion")
    suspend fun getPriceConversion(@QueryMap map: Map<String, String>): Response<String>
}