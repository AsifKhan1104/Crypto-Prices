package com.crypto.prices.remote

import com.crypto.prices.utils.Constants.BASE_URL
import com.crypto.prices.utils.Constants.BASE_URL_CG
import com.crypto.prices.utils.Constants.BASE_URL_NEWS
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class Service {

    fun getCMService(): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

    fun getCMJsonService(): Api{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
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