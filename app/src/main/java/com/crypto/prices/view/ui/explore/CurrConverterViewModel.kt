package com.crypto.prices.view.ui.explore

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepository
import kotlinx.coroutines.launch
import java.io.IOException

class CurrConverterViewModel(
    val app: CryptoApplication,
    private val appRepository: AppRepository,
    map: MutableMap<String, String>
) : ViewModel() {
    val convertedLiveData: MutableLiveData<NetworkResult<String>> = MutableLiveData()
    val suppCurrLiveData: MutableLiveData<NetworkResult<List<String>>> = MutableLiveData()

    init {
    }

    fun getSupportedCurrData() = viewModelScope.launch {
        fetchSupportedCurrData()
    }

    fun getConvertedCurrData(map: MutableMap<String, String>) = viewModelScope.launch {
        fetchConvertedCurrData(map)
    }

    private suspend fun fetchSupportedCurrData() {
        suppCurrLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val response = appRepository.getSupportedCurrList()
                if (response.isSuccessful) {
                    suppCurrLiveData.postValue(response.body()?.let { NetworkResult.Success(it) })
                } else {
                    suppCurrLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                suppCurrLiveData.postValue(NetworkResult.Error(app.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> suppCurrLiveData.postValue(
                    NetworkResult.Error(app.getString(R.string.network_failure))
                )
                else -> suppCurrLiveData.postValue(
                    NetworkResult.Error(app.getString(R.string.conversion_error))
                )
            }
        }
    }

    private suspend fun fetchConvertedCurrData(map: MutableMap<String, String>) {
        convertedLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val response = appRepository.getPriceConversion(map)
                if (response.isSuccessful) {
                    convertedLiveData.postValue(
                        response.body().toString()?.let { NetworkResult.Success(it) })
                } else {
                    convertedLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                convertedLiveData.postValue(NetworkResult.Error(app.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> convertedLiveData.postValue(
                    NetworkResult.Error(app.getString(R.string.network_failure))
                )
                else -> convertedLiveData.postValue(
                    NetworkResult.Error(app.getString(R.string.conversion_error))
                )
            }
        }
    }
}