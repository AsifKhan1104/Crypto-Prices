package com.crypto.prices.view.ui.search

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
    val searchLiveData: MutableLiveData<NetworkResult<SearchData>> = MutableLiveData()

    // We will use a ConflatedBroadcastChannel as this will only broadcast
    // the most recent sent element to all the subscribers
    /*private val searchChanel = ConflatedBroadcastChannel<String>()

    fun setSearchQuery(search: String) {
        //We use .offer() to send the element to all the subscribers.
        searchChanel.offer(search)
    }*/

    init {
        getData("")
    }

    fun getData(query: String) = viewModelScope.launch {
        fetchData(query)

        /*searchChanel.asFlow().flatMapLatest {
            search -> {appRepository.getSearchResults(search) as Flow<SearchData> }
        }*/
    }

    private suspend fun fetchData(query: String) {
        searchLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val response = appRepository.getSearchResults(query)
                if (response.isSuccessful) {
                    searchLiveData.postValue(response.body()?.let { NetworkResult.Success(it) })
                } else {
                    searchLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                searchLiveData.postValue(NetworkResult.Error(application.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.network_failure))
                )
                else -> searchLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.conversion_error))
                )
            }
        }
    }
}