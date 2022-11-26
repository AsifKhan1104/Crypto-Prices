package com.crypto.prices.view.ui.market.crypto.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.model.crypto.search.CryptoDetailData
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepository
import kotlinx.coroutines.launch
import java.io.IOException

class CryptoDetailSearchViewModel(
    app: CryptoApplication,
    private val appRepository: AppRepository,
    map: MutableMap<String, String>
) : ViewModel() {
    val application = app
    val paramMap = map
    val cryptoDetailLiveData: MutableLiveData<NetworkResult<CryptoDetailData>> = MutableLiveData()

    init {
        getData()
    }

    fun getData() = viewModelScope.launch {
        fetchData()
    }

    private suspend fun fetchData() {
        cryptoDetailLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val map: MutableMap<String, String> = HashMap()
                map["localization"] = "false"
                map["tickers"] = "false"
                map["market_data"] = "true"
                map["community_data"] = "false"
                map["developer_data"] = "false"
                map["sparkline"] = "false"

                val response = appRepository.getCryptoCoinDetail(paramMap["id"]!!, map)
                if (response.isSuccessful) {
                    cryptoDetailLiveData.postValue(
                        response.body()?.let { NetworkResult.Success(it) })
                } else {
                    cryptoDetailLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                cryptoDetailLiveData.postValue(NetworkResult.Error(application.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> cryptoDetailLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.network_failure))
                )
                else -> cryptoDetailLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.conversion_error))
                )
            }
        }
    }
}