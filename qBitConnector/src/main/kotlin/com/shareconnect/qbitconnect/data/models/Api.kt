package com.shareconnect.qbitconnect.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String? = null
)

@Serializable
data class AddTorrentRequest(
    val urls: List<String>? = null,
    val torrents: List<String>? = null, // base64 encoded
    val savepath: String? = null,
    val cookie: String? = null,
    val category: String? = null,
    val tags: List<String>? = null,
    val skip_checking: Boolean? = null,
    val paused: Boolean? = null,
    val root_folder: Boolean? = null,
    val rename: String? = null,
    val uploadLimit: Long? = null,
    val downloadLimit: Long? = null,
    val ratioLimit: Float? = null,
    val seedingTimeLimit: Long? = null,
    val autoTMM: Boolean? = null,
    val sequentialDownload: Boolean? = null,
    val firstLastPiecePrio: Boolean? = null
)

@Serializable
data class TorrentActionRequest(
    val hashes: List<String>,
    val deleteFiles: Boolean? = null
)

@Serializable
data class SetCategoryRequest(
    val hashes: List<String>,
    val category: String
)

@Serializable
data class SetTagsRequest(
    val hashes: List<String>,
    val tags: List<String>
)

@Serializable
data class SetLimitsRequest(
    val hashes: List<String>,
    val downloadLimit: Long? = null,
    val uploadLimit: Long? = null
)