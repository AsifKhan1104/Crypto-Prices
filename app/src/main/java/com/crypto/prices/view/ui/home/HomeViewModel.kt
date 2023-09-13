package com.crypto.prices.view.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.prices.CryptoApplication
import com.asf.cryptoprices.R
import com.crypto.prices.model.Trending
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepository
import kotlinx.coroutines.launch
import java.io.IOException

class HomeViewModel(
    app: CryptoApplication,
    private val appRepository: AppRepository
) : ViewModel() {
    val application = app
    val trendingLiveData: MutableLiveData<NetworkResult<Trending>> = MutableLiveData()

    init {
        getTrending()
    }

    fun getTrending() = viewModelScope.launch {
        fetchData()
    }

    private suspend fun fetchData() {
        trendingLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val response = appRepository.getTrendingCoins()
                if (response.isSuccessful) {
                    trendingLiveData.postValue(response.body()?.let { NetworkResult.Success(it) })
                } else {
                    trendingLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                trendingLiveData.postValue(NetworkResult.Error(application.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> trendingLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.network_failure))
                )
                else -> trendingLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.conversion_error))
                )
            }
        }
    }
}