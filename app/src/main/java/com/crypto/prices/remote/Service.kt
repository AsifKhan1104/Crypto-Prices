package com.crypto.prices.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Service {
    private val BASE_URL = "https://pro-api.coinmarketcap.com/";
    private val BASE_URL_CG = "https://api.coingecko.com/api/";
    private val BASE_URL_NEWS = "https://newsapi.org/";

    fun getCMService(): Api{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

    fun getCGService(): Api{
        return Retrofit.Builder()
            .baseUrl(BASE_URL_CG)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

    fun getNewsService(): Api{
        return Retrofit.Builder()
            .baseUrl(BASE_URL_NEWS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }
}