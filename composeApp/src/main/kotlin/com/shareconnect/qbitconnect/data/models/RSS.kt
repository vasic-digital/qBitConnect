package com.shareconnect.qbitconnect.data.models

import kotlinx.datetime.Instant

data class RSSFeed(
    val uid: String,
    val url: String,
    val title: String,
    val lastBuildDate: Instant? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
    val articles: List<RSSArticle> = emptyList()
)

data class RSSArticle(
    val id: String,
    val title: String,
    val description: String? = null,
    val link: String,
    val author: String? = null,
    val category: String? = null,
    val pubDate: Instant? = null,
    val isRead: Boolean = false,
    val torrentUrl: String? = null,
    val size: Long? = null
)

data class RSSRule(
    val name: String,
    val enabled: Boolean = true,
    val mustContain: String = "",
    val mustNotContain: String = "",
    val useRegex: Boolean = false,
    val episodeFilter: String = "",
    val smartFilter: Boolean = false,
    val previouslyMatchedEpisodes: List<String> = emptyList(),
    val affectedFeeds: List<String> = emptyList(),
    val ignoreDays: Int = 0,
    val lastMatch: Instant? = null,
    val addPaused: Boolean = false,
    val category: String = "",
    val savePath: String = "",
    val downloadLimit: Long = -1,
    val uploadLimit: Long = -1,
    val tags: List<String> = emptyList()
)