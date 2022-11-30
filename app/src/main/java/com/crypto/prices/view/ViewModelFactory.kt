package com.crypto.prices.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crypto.prices.CryptoApplication
import com.crypto.prices.view.ui.explore.CurrConverterViewModel
import com.crypto.prices.view.ui.explore.NewsViewModel
import com.crypto.prices.view.ui.home.HomeViewModel
import com.crypto.prices.view.ui.market.categories.CategoriesViewModel
import com.crypto.prices.view.ui.market.crypto.CryptoViewModel
import com.crypto.prices.view.ui.market.crypto.detail.CryptoDetailSearchViewModel
import com.crypto.prices.view.ui.market.crypto.detail.CryptoDetailViewModel
import com.crypto.prices.view.ui.market.derivatives.DerivativesViewModel
import com.crypto.prices.view.ui.market.derivatives.detail.DerivativesDetailViewModel
import com.crypto.prices.view.ui.market.exchanges.ExchangesViewModel
import com.crypto.prices.view.ui.market.exchanges.detail.ExchangesDetailSearchViewModel
import com.crypto.prices.view.ui.market.exchanges.detail.ExchangesDetailViewModel
import com.crypto.prices.view.ui.market.nfts.NftViewModel
import com.crypto.prices.view.ui.market.nfts.detail.NftDetailViewModel
import com.crypto.prices.view.ui.more.CurrencySelectViewModel
import com.crypto.prices.view.ui.search.SearchViewModel

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

        if (modelClass.isAssignableFrom(CryptoDetailSearchViewModel::class.java)) {
            return CryptoDetailSearchViewModel(app, appRepository, map) as T
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

        if (modelClass.isAssignableFrom(DerivativesViewModel::class.java)) {
            return DerivativesViewModel(app, appRepository, map) as T
        }

        if (modelClass.isAssignableFrom(DerivativesDetailViewModel::class.java)) {
            return DerivativesDetailViewModel(app, appRepository, map) as T
        }

        if (modelClass.isAssignableFrom(ExchangesDetailViewModel::class.java)) {
            return ExchangesDetailViewModel(app, appRepository, map) as T
        }

        if (modelClass.isAssignableFrom(ExchangesDetailSearchViewModel::class.java)) {
            return ExchangesDetailSearchViewModel(app, appRepository, map) as T
        }

        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(app, appRepository, map) as T
        }

        if (modelClass.isAssignableFrom(CurrConverterViewModel::class.java)) {
            return CurrConverterViewModel(app, appRepository, map) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}