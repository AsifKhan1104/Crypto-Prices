package com.crypto.prices.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crypto.prices.CryptoApplication
import com.crypto.prices.view.ui.explore.NewsViewModel
import com.crypto.prices.view.ui.home.HomeViewModel
import com.crypto.prices.view.ui.market.categories.CategoriesViewModel
import com.crypto.prices.view.ui.market.crypto.CryptoViewModel
import com.crypto.prices.view.ui.market.crypto.detail.CryptoDetailViewModel
import com.crypto.prices.view.ui.market.exchanges.ExchangesViewModel
import com.crypto.prices.view.ui.market.nfts.NftViewModel
import com.crypto.prices.view.ui.market.nfts.detail.NftDetailViewModel
import com.crypto.prices.view.ui.more.CurrencySelectViewModel

class ViewModelFactory(
    val app: CryptoApplication,
    val appRepository: AppRepository,
    val map: MutableMap<String, String>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CryptoViewModel::class.java)) {
            return CryptoViewModel(app, appRepository, map) as T
        }

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(app, appRepository) as T
        }

        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            return NewsViewModel(app, appRepository) as T
        }

        if (modelClass.isAssignableFrom(CryptoDetailViewModel::class.java)) {
            return CryptoDetailViewModel(app, appRepository, map) as T
        }

        if (modelClass.isAssignableFrom(CategoriesViewModel::class.java)) {
            return CategoriesViewModel(app, appRepository, map) as T
        }

        if (modelClass.isAssignableFrom(NftViewModel::class.java)) {
            return NftViewModel(app, appRepository, map) as T
        }

        if (modelClass.isAssignableFrom(NftDetailViewModel::class.java)) {
            return NftDetailViewModel(app, appRepository, map) as T
        }

        if (modelClass.isAssignableFrom(CurrencySelectViewModel::class.java)) {
            return CurrencySelectViewModel(app, appRepository, map) as T
        }

        if (modelClass.isAssignableFrom(ExchangesViewModel::class.java)) {
            return ExchangesViewModel(app, appRepository, map) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}