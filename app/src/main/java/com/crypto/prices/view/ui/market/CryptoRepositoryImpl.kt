package com.crypto.prices.view.ui.market

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crypto.prices.model.ListingsLatest
import com.crypto.prices.remote.Service
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CryptoRepositoryImpl : CryptoRepository {
    override fun getCryptoPrices(): LiveData<NetworkResult<ListingsLatest>?>? {
        var liveData: MutableLiveData<NetworkResult<ListingsLatest>> = MutableLiveData()
        liveData.value = NetworkResult.Loading()
        if (Utility.isInternetAvailable()) {
            try {
                val service = Service().getUsersService()
                CoroutineScope(Dispatchers.IO).launch {
                    val response = service.getListingsLatest()
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            liveData.value =
                                NetworkResult.Success(response.body() as ListingsLatest)
                        } else {
                            liveData.value = NetworkResult.Error(response.message())
                        }
                    }
                }
            } catch (e: Exception) {
                liveData.value = NetworkResult.Error(e.message)
            }
        } else {
            liveData.value = NetworkResult.Error("No Internet connection !")
        }
        return liveData
    }
}