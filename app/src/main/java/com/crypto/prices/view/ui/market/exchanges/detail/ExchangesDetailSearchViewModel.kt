package com.crypto.prices.view.ui.market.exchanges.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.model.ExchangeDataSearch
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepository
import kotlinx.coroutines.launch
import java.io.IOException

class ExchangesDetailSearchViewModel(
    val app: CryptoApplication,
    private val appRepository: AppRepository,
    val map: MutableMap<String, String>
) : ViewModel() {
    val exchangeLiveData: MutableLiveData<NetworkResult<ExchangeDataSearch>> =
        MutableLiveData()

    init {
        getData()
    }

    fun getData() = viewModelScope.launch {
        fetchData()
    }

    private suspend fun fetchData() {
        exchangeLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val response = appRepository.getExchange(map["id"]!!)
                if (response.isSuccessful) {
                    exchangeLiveData.postValue(
                        response.body()?.let { NetworkResult.Success(it) })
                } else {
                    exchangeLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                exchangeLiveData.postValue(NetworkResult.Error(app.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> exchangeLiveData.postValue(
                    NetworkResult.Error(app.getString(R.string.network_failure))
                )
                else -> exchangeLiveData.postValue(
                    NetworkResult.Error(app.getString(R.string.conversion_error))
                )
            }
        }
    }
}