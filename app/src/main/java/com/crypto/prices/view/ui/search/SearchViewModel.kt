package com.crypto.prices.view.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.model.SearchData
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepository
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    app: CryptoApplication,
    private val appRepository: AppRepository,
    map: MutableMap<String, String>
) : ViewModel() {
    val application = app
    private val searchMutableLiveData: MutableLiveData<NetworkResult<SearchData>> = MutableLiveData()
    private lateinit var searchLiveData: LiveData<NetworkResult<SearchData>>

    fun getData(query: String) = viewModelScope.launch {
        fetchData(query)
    }

    private suspend fun fetchData(query: String) {
        searchMutableLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val response = appRepository.getSearchResults(query)
                if (response.isSuccessful) {
                    searchMutableLiveData.postValue(response.body()?.let { NetworkResult.Success(it) })
                } else {
                    searchMutableLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                searchMutableLiveData.postValue(NetworkResult.Error(application.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchMutableLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.network_failure))
                )
                else -> searchMutableLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.conversion_error))
                )
            }
        }
    }

    fun getSearchResults():LiveData<NetworkResult<SearchData>> {
        searchLiveData = searchMutableLiveData
        return searchLiveData
    }
}