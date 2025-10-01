package com.shareconnect.qbitconnect.model

sealed class RequestResult<out T> {
    data class Success<T>(val data: T) : RequestResult<T>()
    sealed class Error : RequestResult<Nothing>() {
        sealed class RequestError : Error() {
            data object Banned : RequestError()
            data object InvalidCredentials : RequestError()
            data class UnknownLoginResponse(val response: String) : RequestError()
        }
        data class ApiError(val code: Int) : Error()
        data class NetworkError(val throwable: Throwable) : Error()
    }
}