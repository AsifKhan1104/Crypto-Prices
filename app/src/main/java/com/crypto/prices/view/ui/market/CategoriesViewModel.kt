package com.crypto.prices.view.ui.market

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.model.CategoriesData
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepository
import kotlinx.coroutines.launch
import java.io.IOException

class CategoriesViewModel(
    app: CryptoApplication,
    private val appRepository: AppRepository,
    map: MutableMap<String, String>
) : ViewModel() {
    val application = app
    val paramMap = map
    val categoriesLiveData: MutableLiveData<NetworkResult<List<CategoriesData>>> = MutableLiveData()

    init {
        getDataViaApi(paramMap)
    }

    fun getDataViaApi(mp: MutableMap<String, String>) = viewModelScope.launch {
        fetchData(mp)
    }

    private suspend fun fetchData(latestMap: MutableMap<String, String>) {
        categoriesLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val response = appRepository.getCategories(latestMap)
                if (response.isSuccessful) {
                    categoriesLiveData.postValue(response.body()?.let { NetworkResult.Success(it) })
                } else {
                    categoriesLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                categoriesLiveData.postValue(NetworkResult.Error(application.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> categoriesLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.network_failure))
                )
                else -> categoriesLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.conversion_error))
                )
            }
        }
    }
}