package com.crypto.prices.view.ui.explore

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.model.NewsData
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepository
import kotlinx.coroutines.launch
import java.io.IOException

class NewsViewModel(
    app: CryptoApplication,
    private val appRepository: AppRepository
) : ViewModel() {
    val application = app
    val newsLiveData: MutableLiveData<NetworkResult<NewsData>> = MutableLiveData()

    init {
        getNews()
    }

    fun getNews() = viewModelScope.launch {
        fetchData()
    }

    private suspend fun fetchData() {
        newsLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val map: MutableMap<String, String> = HashMap()
                map["q"] = "Crypto"
                map["apiKey"] = "0bfddc1dafb24e8b883576fba4b58235"
                map["sortBy"] = "publishedAt"

                val response = appRepository.getAllNews(map)
                if (response.isSuccessful) {
                    newsLiveData.postValue(response.body()?.let { NetworkResult.Success(it) })
                } else {
                    newsLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                newsLiveData.postValue(NetworkResult.Error(application.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> newsLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.network_failure))
                )
                else -> newsLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.conversion_error))
                )
            }
        }
    }
}