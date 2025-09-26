package com.shareconnect.qbitconnect.model

import com.shareconnect.qbitconnect.model.serializers.InstantSerializer
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Article(
    val id: String,
    val title: String,
    val description: String?,
    val torrentUrl: String,
    val isRead: Boolean,
    @Serializable(with = InstantSerializer::class)
    val date: Instant,
    val path: List<String>,
)
