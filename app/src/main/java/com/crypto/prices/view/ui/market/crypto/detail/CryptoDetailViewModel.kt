package com.crypto.prices.view.ui.market.crypto.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.prices.CryptoApplication
import com.asf.cryptoprices.R
import com.crypto.prices.model.CryptoChartData
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepository
import kotlinx.coroutines.launch
import java.io.IOException

class CryptoDetailViewModel(
    app: CryptoApplication,
    private val appRepository: AppRepository,
    map: MutableMap<String, String>
) : ViewModel() {
    val application = app
    val paramMap = map
    val cryptoChartLiveData: MutableLiveData<NetworkResult<CryptoChartData>> = MutableLiveData()

    init {
        getCryptoChart("-1")
    }

    fun getCryptoChart(days: String) = viewModelScope.launch {
        fetchData(days)
    }

    private suspend fun fetchData(days: String) {
        cryptoChartLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val map: MutableMap<String, String> = HashMap()
                map["vs_currency"] = paramMap["currency"]!!
                if (days.equals("-1")) {
                    map["days"] = paramMap["days"]!!
                } else {
                    map["days"] = days
                }

                val response = appRepository.getCryptoPricesChart(paramMap["symbol"]!!, map)
                if (response.isSuccessful) {
                    cryptoChartLiveData.postValue(
                        response.body()?.let { NetworkResult.Success(it) })
                } else {
                    cryptoChartLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                cryptoChartLiveData.postValue(NetworkResult.Error(application.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> cryptoChartLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.network_failure))
                )
                else -> cryptoChartLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.conversion_error))
                )
            }
        }
    }
}