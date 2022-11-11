package com.crypto.prices.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.crypto.prices.CryptoApplication
import com.crypto.prices.view.ui.home.HomeViewModel
import com.crypto.prices.view.ui.market.CryptoViewModel

class ViewModelFactory(
    val app: CryptoApplication,
    val appRepository: AppRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CryptoViewModel::class.java)) {
            return CryptoViewModel(app, appRepository) as T
        }

        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(app, appRepository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}