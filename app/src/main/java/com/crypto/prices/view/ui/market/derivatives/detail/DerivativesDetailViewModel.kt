package com.crypto.prices.view.ui.market.derivatives.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crypto.prices.CryptoApplication
import com.asf.cryptoprices.R
import com.crypto.prices.model.DerivativesDetailData
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepository
import kotlinx.coroutines.launch
import java.io.IOException

class DerivativesDetailViewModel(
    val app: CryptoApplication,
    private val appRepository: AppRepository,
    val map: MutableMap<String, String>
) : ViewModel() {
    val paramMap = map
    val derivativesDetailLiveData: MutableLiveData<NetworkResult<List<DerivativesDetailData>>> =
        MutableLiveData()

    init {
        getData("all")
    }

    fun getData(tickers: String) = viewModelScope.launch {
        fetchData(tickers)
    }

    private suspend fun fetchData(tickers: String) {
        derivativesDetailLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val map = HashMap<String, String>()
                map["include_tickers"] = tickers

                val response = appRepository.getDerivativesDetail(map)
                if (response.isSuccessful) {
                    derivativesDetailLiveData.postValue(
                        response.body()?.let { NetworkResult.Success(it) })
                } else {
                    derivativesDetailLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                derivativesDetailLiveData.postValue(NetworkResult.Error(app.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> derivativesDetailLiveData.postValue(
                    NetworkResult.Error(app.getString(R.string.network_failure))
                )
                else -> derivativesDetailLiveData.postValue(
                    NetworkResult.Error(app.getString(R.string.conversion_error))
                )
            }
        }
    }
}