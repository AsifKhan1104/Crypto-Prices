package com.crypto.prices.view.ui.market

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.model.CryptoDataa
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepository
import kotlinx.coroutines.launch
import java.io.IOException

class CryptoViewModel(
    app: CryptoApplication,
    private val appRepository: AppRepository
) : ViewModel() {
    val application = app
    val cryptoLiveData: MutableLiveData<NetworkResult<List<CryptoDataa>>> = MutableLiveData()

    init {
        getCrypto()
    }

    fun getCrypto() = viewModelScope.launch {
        fetchData()
    }

    private suspend fun fetchData() {
        cryptoLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val map: MutableMap<String, String> = HashMap()
                map["vs_currency"] = "usd"
                /*map["order"] = "market_cap_desc"
                map["per_page"] = 100
                map["page"] = 1
                map["sparkline"] = false*/

                val response = appRepository.getCryptoPrices(map)
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