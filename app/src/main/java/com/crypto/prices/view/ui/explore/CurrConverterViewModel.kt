package com.crypto.prices.view.ui.explore

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asf.cryptoprices.R
import com.crypto.prices.CryptoApplication
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.data.online.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CurrConverterViewModel @Inject constructor(
    val app: CryptoApplication,
    private val appRepository: AppRepository
) : ViewModel() {
    val convertedLiveData: MutableLiveData<NetworkResult<String>> = MutableLiveData()
    val suppCurrLiveData: MutableLiveData<NetworkResult<List<String>>> = MutableLiveData()

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
                    var currList = response.body()?.let { it }!!
                    if (currList.isNotEmpty()) {
                        var capsCurrList = ArrayList<String>()
                        for (items in currList) {
                            capsCurrList.add(items.uppercase())
                        }
                        suppCurrLiveData.postValue(NetworkResult.Success(capsCurrList))
                    } else {
                        suppCurrLiveData.postValue(NetworkResult.Error("No Data found !!"))
                    }
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
                        response.body().toString().let { NetworkResult.Success(it) })
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