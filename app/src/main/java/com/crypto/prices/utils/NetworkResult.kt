package com.crypto.prices.utils

sealed class NetworkResult<T>(
    val networkData: T? = null,
    val networkErrorMessage: String? = null
) {

    class Success<T>(networkData: T) : NetworkResult<T>(networkData)

    class Error<T>(networkErrorMessage: String?, data: T? = null) : NetworkResult<T>(data, networkErrorMessage)

    class Loading<T> : NetworkResult<T>()

}
