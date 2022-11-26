package com.crypto.prices.view.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.model.SearchData
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException

class SearchViewModel(
    val app: CryptoApplication,
    private val appRepository: AppRepository,
    map: MutableMap<String, String>
) : ViewModel() {
    private val searchStateFlow =
        MutableStateFlow<NetworkResult<SearchData>>(NetworkResult.Error(app.getString(R.string.no_results_found)))

    fun fetchData(query: String) {
        if (!Utility.isInternetAvailable()) {
            searchStateFlow.value = NetworkResult.Error(app.getString(R.string.no_internet_msg))
        } else {
            viewModelScope.launch {
                appRepository.getSearchResults(query)
                    .flowOn(Dispatchers.Default)
                    .catch { e ->
                        when (e) {
                            is IOException -> searchStateFlow.value =
                                NetworkResult.Error(app.getString(R.string.network_failure))
                            else -> searchStateFlow.value =
                                NetworkResult.Error(app.getString(R.string.conversion_error))
                        }
                    }
                    .distinctUntilChanged()
                    .debounce(1000)
                    .collectLatest {
                        if (it != null && it?.body() != null) {
                            searchStateFlow.value = NetworkResult.Success(it?.body()!!)
                        } else {
                            searchStateFlow.value =
                                NetworkResult.Error(app.getString(R.string.error_msg_retry))
                        }
                    }
            }
        }
    }

    fun getSearchResults(): StateFlow<NetworkResult<SearchData>> {
        return searchStateFlow
    }
}