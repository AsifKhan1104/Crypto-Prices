package com.crypto.prices.view.ui.market

import androidx.lifecycle.LiveData
import com.crypto.prices.model.ListingsLatest
import com.crypto.prices.utils.NetworkResult


interface CryptoRepository {
    fun getCryptoPrices(): LiveData<NetworkResult<ListingsLatest>?>?
}