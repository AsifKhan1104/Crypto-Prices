package com.crypto.prices.view.ui.market.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.crypto.prices.model.CryptoData
import com.crypto.prices.view.AppRepository

class CryptoPagingSource(
    val appRepository: AppRepository,
) : PagingSource<Int, CryptoData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CryptoData> {
        try {
            // Start refresh at page 1, if undefined.
            val pageNumber = params.key ?: 1
            val map = HashMap<String, String> ()
            map.put("vs_currency", "usd")
            map["per_page"] = "5"
            map["page"] = pageNumber.toString()
            val response = (appRepository.getCryptoPrices(map)).body()

            return LoadResult.Page(
                data = response!!,
                prevKey = if (pageNumber == 1) null else pageNumber - 1,
                nextKey = pageNumber + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CryptoData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
        //return null
    }
}