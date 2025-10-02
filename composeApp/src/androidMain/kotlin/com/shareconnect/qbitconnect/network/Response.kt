package com.shareconnect.qbitconnect.network

data class Response<T>(
    val code: Int,
    val body: T?
)