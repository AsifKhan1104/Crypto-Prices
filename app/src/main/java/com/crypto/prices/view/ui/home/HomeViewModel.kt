package com.crypto.prices.view.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asf.cryptoprices.R
import com.crypto.prices.CryptoApplication
import com.crypto.prices.data.offline.Watchlist
import com.crypto.prices.model.Trending
import com.crypto.prices.utils.NetworkResult
import com.crypto.prices.utils.Utility
import com.crypto.prices.data.online.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val app: CryptoApplication,
    private val appRepository: AppRepository
) : ViewModel() {
    val application = app
    val trendingLiveData: MutableLiveData<NetworkResult<Trending>> = MutableLiveData()
    val watchListLiveData: MutableLiveData<List<Watchlist>> = MutableLiveData()

    init {
        getTrending()
    }

    private fun getTrending() = viewModelScope.launch {
        fetchData()
    }

    private suspend fun fetchData() {
        trendingLiveData.postValue(NetworkResult.Loading())
        try {
            if (Utility.isInternetAvailable()) {
                val response = appRepository.getTrendingCoins()
                if (response.isSuccessful) {
                    trendingLiveData.postValue(response.body()?.let { NetworkResult.Success(it) })
                } else {
                    trendingLiveData.postValue(NetworkResult.Error(response.message()))
                }
            } else {
                trendingLiveData.postValue(NetworkResult.Error(application.getString(R.string.no_internet_msg)))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> trendingLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.network_failure))
                )

                else -> trendingLiveData.postValue(
                    NetworkResult.Error(application.getString(R.string.conversion_error))
                )
            }
        }
    }

    fun fetchUpdatedPricesApi(watchlist: List<Watchlist>) {
        val cryptoIds = StringBuilder()
        for (i in watchlist) {
            if (!i.type.equals("") && i.type.equals("crypto"))
                cryptoIds.append(i.id + ",")
        }

        // now request these id's data
        val map = HashMap<String, String?>()
        map["vs_currency"] = Utility.getCurrencyGlobal(app)
        map["order"] = "market_cap_desc"
        //map["per_page"] = "20"
        map["ids"] = cryptoIds.toString()

        viewModelScope.launch {
            val response = appRepository.getCryptoPrices(map as MutableMap<String, String>)
            // check if request is successful
            if (response.isSuccessful) {
                val cryptoList = response.body()!!
                // set updated price & price_change_24%
                for ((i, value1) in cryptoList.withIndex()) {
                    for ((j, value2) in watchlist.withIndex()) {
                        if (value1.id.equals(value2.id)) {
                            watchlist[j].price = cryptoList[i].current_price.toString()
                            watchlist[j].priceChange24h =
                                cryptoList[i].price_change_percentage_24h.toString()
                        }
                    }
                }
                watchListLiveData.postValue(watchlist)
            }
        }
    }
}