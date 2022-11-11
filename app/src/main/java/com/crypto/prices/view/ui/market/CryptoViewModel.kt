package com.crypto.prices.view.ui.market

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.crypto.prices.model.ListingsLatest
import com.crypto.prices.utils.NetworkResult

class CryptoViewModel : ViewModel() {
    private var mNetworkResult: MediatorLiveData<NetworkResult<ListingsLatest>>? = null
    private var mCryptoRepository: CryptoRepository? = null

    init {
        mNetworkResult = MediatorLiveData<NetworkResult<ListingsLatest>>()
        mCryptoRepository = CryptoRepositoryImpl()
    }

    fun getNetworkResult(): LiveData<NetworkResult<ListingsLatest>> {
        return mNetworkResult!!
    }

    fun loadCryptoData(): LiveData<NetworkResult<ListingsLatest>>? {
        /*mNetworkResult?.addSource(
            mCryptoRepository.getCryptoPrices()
        ) { apiResponse -> mNetworkResult.setValue(apiResponse) }*/
        mNetworkResult?.value = mCryptoRepository?.getCryptoPrices()?.value
        return mNetworkResult
    }
}