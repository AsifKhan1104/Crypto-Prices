package com.crypto.prices.view.ui.market

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.prices.CryptoApplication
import com.crypto.prices.R
import com.crypto.prices.model.NftData
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepository
import kotlinx.coroutines.launch
import java.io.IOException

class NftViewModel(
    app: CryptoApplication,
    private val appRepository: AppRepository,
    map: MutableMap<String, String>
) : ViewModel() {
    val application = app
    val paramMap = map
    val nftLiveData: MutableLiveData<NetworkResult<List<NftData>>> = MutableLiveData()

    init {
        getData(paramMap)
    }

    fun getData(mp:MutableMap<String, String>) = viewModelScope.launch {
        fetchData(mp)
    }

    private suspend fun fetchData(latestMap: MutableMap<String, String>) {
        nftLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val response = appRepository.getNfts(latestMap)
                if (response.isSuccessful) {
                    nftLiveData.postValue(response.body()?.let { NetworkResult.Success(it) })
                } else {
                    nftLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                nftLiveData.postValue(NetworkResult.Error(application.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> nftLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.network_failure))
                )
                else -> nftLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.conversion_error))
                )
            }
        }
    }
}