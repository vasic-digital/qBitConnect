package com.shareconnect.qbitconnect.model

import kotlinx.serialization.Serializable

@Serializable
data class RssFeed(
    val name: String,
    val uid: String,
    val url: String,
)
