package com.crypto.prices.remote

import com.crypto.prices.model.NewsData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 * Created by Asif Khan on 13/09/23.
 */
interface NewsApi {
    @GET("v2/everything")
    suspend fun getAllNews(@QueryMap map: Map<String, String>): Response<NewsData>
}