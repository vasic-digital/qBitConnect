package com.shareconnect.qbitconnect.data.models

data class Server(
    val id: String,
    val name: String,
    val host: String,
    val port: Int,
    val username: String? = null,
    val password: String? = null,
    val useHttps: Boolean = false,
    val isActive: Boolean = false
)