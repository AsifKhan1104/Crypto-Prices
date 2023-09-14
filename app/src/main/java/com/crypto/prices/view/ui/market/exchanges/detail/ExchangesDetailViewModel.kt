package com.crypto.prices.view.ui.market.exchanges.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asf.cryptoprices.R
import com.crypto.prices.CryptoApplication
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.data.online.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ExchangesDetailViewModel @Inject constructor(
    val app: CryptoApplication,
    private val appRepository: AppRepository
) : ViewModel() {
    private lateinit var paramMap: MutableMap<String, String>
    val exchangesChartLiveData: MutableLiveData<NetworkResult<ArrayList<ArrayList<BigDecimal>>>> =
        MutableLiveData()

    fun initiateChart(map: MutableMap<String, String>) {
        paramMap = map
        getChart("-1")
    }

    fun getChart(days: String) = viewModelScope.launch {
        fetchData(days)
    }

    private suspend fun fetchData(days: String) {
        exchangesChartLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val map = HashMap<String, String>()
                if (days.equals("-1")) {
                    map["days"] = paramMap["days"]!!
                } else {
                    map["days"] = days
                }

                val response = appRepository.getExchangesChart(paramMap["id"]!!, map)
                if (response.isSuccessful) {
                    exchangesChartLiveData.postValue(
                        response.body()?.let { NetworkResult.Success(it) })
                } else {
                    exchangesChartLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                exchangesChartLiveData.postValue(NetworkResult.Error(app.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> exchangesChartLiveData.postValue(
                    NetworkResult.Error(app.getString(R.string.network_failure))
                )

                else -> exchangesChartLiveData.postValue(
                    NetworkResult.Error(app.getString(R.string.conversion_error))
                )
            }
        }
    }
}