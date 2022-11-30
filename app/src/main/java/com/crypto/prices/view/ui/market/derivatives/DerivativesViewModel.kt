package com.crypto.prices.view.ui.market.derivatives

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.crypto.prices.CryptoApplication
import com.crypto.prices.model.DerivativesData
import com.crypto.prices.utils.Constants
import com.crypto.prices.utils.Utility
import com.crypto.prices.view.AppRepository
import kotlinx.coroutines.flow.Flow

class DerivativesViewModel(
    app: CryptoApplication,
    private val appRepository: AppRepository,
    map: MutableMap<String, String>
) : ViewModel() {
    lateinit var derivativesList: Flow<PagingData<DerivativesData>>
    var hasInternet = true

    init {
        if (Utility.isInternetAvailable()) {
            loadPagingData(map)
        } else {
            hasInternet = false
        }
    }

    fun loadPagingData(map: MutableMap<String, String>) {
        derivativesList = Pager(PagingConfig(pageSize = Constants.itemsPerPage.toInt())) {
            DerivativesPagingSource(appRepository, map)
        }.flow
            .cachedIn(viewModelScope)
    }
}