package com.crypto.prices.di

import com.crypto.prices.view.AppRepository
import com.crypto.prices.view.AppRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by Asif Khan on 13/09/23.
 */

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun appRepository(appRepositoryImpl: AppRepositoryImpl): AppRepository
}