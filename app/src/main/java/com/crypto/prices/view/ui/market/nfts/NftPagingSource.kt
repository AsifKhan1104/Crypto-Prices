package com.crypto.prices.view.ui.market.nfts

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.crypto.prices.model.NftData
import com.crypto.prices.view.AppRepository

class NftPagingSource(
    val appRepository: AppRepository,
    val map: MutableMap<String, String>
) : PagingSource<Int, NftData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NftData> {
        try {
            // Start refresh at page 1, if undefined.
            val pageNumber = params.key ?: 1
            var end = false
            map["page"] = pageNumber.toString()
            val res = appRepository.getNfts(map)
            var response: List<NftData>? = null
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

    override fun getRefreshKey(state: PagingState<Int, NftData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}