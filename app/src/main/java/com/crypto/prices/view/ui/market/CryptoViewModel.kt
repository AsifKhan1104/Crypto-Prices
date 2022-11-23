package com.crypto.prices.view.ui.market

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.model.CryptoData
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepository
import com.crypto.prices.view.ui.market.paging.CryptoPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.IOException

class CryptoViewModel(
    app: CryptoApplication,
    private val appRepository: AppRepository,
    map: MutableMap<String, String>
) : ViewModel() {
    val application = app
    val paramMap = map
    val cryptoLiveData: MutableLiveData<NetworkResult<List<CryptoData>>> = MutableLiveData()
    lateinit var cryptoList:Flow<PagingData<CryptoData>>

    init {
        //getCrypto(paramMap)
        loadPagingData()
    }

    private fun loadPagingData() {
        cryptoList = Pager(PagingConfig(pageSize = 5)) {
            CryptoPagingSource(appRepository)
        }.flow
            .cachedIn(viewModelScope)


    }

    fun getCrypto(mp:MutableMap<String, String>) = viewModelScope.launch {
        fetchData(mp)
    }

    private suspend fun fetchData(latestMap: MutableMap<String, String>) {
        cryptoLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val response = appRepository.getCryptoPrices(latestMap)
                if (response.isSuccessful) {
                    cryptoLiveData.postValue(response.body()?.let { NetworkResult.Success(it) })
                } else {
                    cryptoLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                cryptoLiveData.postValue(NetworkResult.Error(application.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> cryptoLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.network_failure))
                )
                else -> cryptoLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.conversion_error))
                )
            }
        }
    }
}