package com.crypto.prices.view.ui.market.categories

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asf.cryptoprices.R
import com.crypto.prices.CryptoApplication
import com.crypto.prices.model.CategoriesData
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.data.online.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    app: CryptoApplication,
    private val appRepository: AppRepository
) : ViewModel() {
    val application = app
    val categoriesLiveData: MutableLiveData<NetworkResult<List<CategoriesData>>> = MutableLiveData()

    fun fetchDataViaApi(mp: MutableMap<String, String>) = viewModelScope.launch {
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