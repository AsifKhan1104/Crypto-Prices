package com.crypto.prices.di

import com.crypto.prices.CryptoApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Asif Khan on 13/09/23.
 */

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun providesCryptoApplication(): CryptoApplication {
        return CryptoApplication.instance!!
    }
}