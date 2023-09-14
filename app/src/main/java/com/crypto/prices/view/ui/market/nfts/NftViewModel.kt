package com.crypto.prices.view.ui.market.nfts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.crypto.prices.CryptoApplication
import com.crypto.prices.model.NftData
import com.crypto.prices.utils.Constants
import com.crypto.prices.utils.Utility
import com.crypto.prices.data.online.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NftViewModel @Inject constructor(
    app: CryptoApplication,
    private val appRepository: AppRepository
) : ViewModel() {
    lateinit var nftList: Flow<PagingData<NftData>>
    var hasInternet = true

    fun loadData(map: MutableMap<String, String>) {
        if (Utility.isInternetAvailable()) {
            loadPagingData(map)
        } else {
            hasInternet = false
        }
    }

    private fun loadPagingData(map: MutableMap<String, String>) {
        nftList = Pager(PagingConfig(pageSize = Constants.itemsPerPage.toInt())) {
            NftPagingSource(appRepository, map)
        }.flow
            .cachedIn(viewModelScope)
    }
}