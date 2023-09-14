package com.crypto.prices.view.ui.market.derivatives

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.crypto.prices.model.DerivativesData
import com.crypto.prices.data.online.AppRepository

class DerivativesPagingSource(
    val appRepository: AppRepository,
    val map: MutableMap<String, String>
) : PagingSource<Int, DerivativesData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DerivativesData> {
        try {
            // Start refresh at page 1, if undefined.
            val pageNumber = params.key ?: 1
            var end = false
            map["page"] = pageNumber.toString()
            val res = appRepository.getDerivatives(map)
            var response: List<DerivativesData>? = null
            // check if request is successful
            if (res.isSuccessful) {
                response = res.body()
                // if there is end of dataset, set nextKey as null
                if (response?.size == 0)
                    end = true
            }

            return LoadResult.Page(
                data = response!!,
                prevKey = if (pageNumber == 1) null else pageNumber - 1,
                nextKey = if (!end) pageNumber + 1 else null
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DerivativesData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}