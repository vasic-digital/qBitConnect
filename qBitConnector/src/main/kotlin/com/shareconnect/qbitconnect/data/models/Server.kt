package com.shareconnect.qbitconnect.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Server(
    val id: Int,
    val name: String,
    val host: String,
    val port: Int,
    val username: String? = null,
    val password: String? = null,
    val useHttps: Boolean = false,
    val isActive: Boolean = false
) {
    val requestUrl = buildString {
        if (!host.contains("://")) {
            append(if (useHttps) "https://" else "http://")
        }
        append(host)
        if ((useHttps && port != 443) || (!useHttps && port != 80)) {
            append(":$port")
        }
        if (!endsWith("/")) {
            append("/")
        }
    }
}