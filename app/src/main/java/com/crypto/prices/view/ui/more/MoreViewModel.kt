package com.crypto.prices.view.ui.more

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.model.exchangeRates.ExchangeRates
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepository
import kotlinx.coroutines.launch
import java.io.IOException

class MoreViewModel(
    app: CryptoApplication,
    private val appRepository: AppRepository,
    map: MutableMap<String, String>
) : ViewModel() {
    val application = app
    val paramMap = map
    val exchangeRateLiveData: MutableLiveData<NetworkResult<ExchangeRates>> = MutableLiveData()

    init {
        getDataViaApi(paramMap)
    }

    fun getDataViaApi(mp:MutableMap<String, String>) = viewModelScope.launch {
        fetchData(mp)
    }

    private suspend fun fetchData(latestMap: MutableMap<String, String>) {
        exchangeRateLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val response = appRepository.getExchangeRates()
                if (response.isSuccessful) {
                    exchangeRateLiveData.postValue(response.body()?.let { NetworkResult.Success(it) })
                } else {
                    exchangeRateLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                exchangeRateLiveData.postValue(NetworkResult.Error(application.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> exchangeRateLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.network_failure))
                )
                else -> exchangeRateLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.conversion_error))
                )
            }
        }
    }
}