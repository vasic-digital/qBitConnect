package com.shareconnect.qbitconnect.model

import kotlinx.serialization.Serializable

@Serializable
data class TorrentWebSeed(
    val url: String,
)
