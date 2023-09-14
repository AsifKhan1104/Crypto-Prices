package com.crypto.prices.view.ui.market.nfts.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asf.cryptoprices.R
import com.crypto.prices.CryptoApplication
import com.crypto.prices.model.NftDetailData
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.data.online.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NftDetailViewModel @Inject constructor(
    app: CryptoApplication,
    private val appRepository: AppRepository
) : ViewModel() {
    val application = app
    val nftDetailLiveData: MutableLiveData<NetworkResult<NftDetailData>> = MutableLiveData()

    fun getData(mp:MutableMap<String, String>) = viewModelScope.launch {
        fetchData(mp)
    }

    private suspend fun fetchData(latestMap: MutableMap<String, String>) {
        val id = latestMap.get("id")!!
        nftDetailLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val response = appRepository.getNftData(id)
                if (response.isSuccessful) {
                    nftDetailLiveData.postValue(response.body()?.let { NetworkResult.Success(it) })
                } else {
                    nftDetailLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                nftDetailLiveData.postValue(NetworkResult.Error(application.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> nftDetailLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.network_failure))
                )
                else -> nftDetailLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.conversion_error))
                )
            }
        }
    }
}