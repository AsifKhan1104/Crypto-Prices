package com.crypto.prices.view.ui.more

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asf.cryptoprices.R
import com.crypto.prices.CryptoApplication
import com.crypto.prices.model.exchangeRates.ExchangeRates
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.data.online.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CurrencySelectViewModel @Inject constructor(
    app: CryptoApplication,
    private val appRepository: AppRepository
) : ViewModel() {
    val application = app
    val exchangeRateLiveData: MutableLiveData<NetworkResult<ExchangeRates>> = MutableLiveData()

    fun getDataViaApi() = viewModelScope.launch {
        fetchData()
    }

    private suspend fun fetchData() {
        exchangeRateLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val response = appRepository.getExchangeRates()
                if (response.isSuccessful) {
                    exchangeRateLiveData.postValue(
                        response.body()?.let { NetworkResult.Success(it) })
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