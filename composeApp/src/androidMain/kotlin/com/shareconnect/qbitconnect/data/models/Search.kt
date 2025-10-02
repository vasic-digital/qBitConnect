package com.shareconnect.qbitconnect.data.models

data class SearchPlugin(
    val name: String,
    val fullName: String,
    val url: String,
    val enabled: Boolean = true,
    val version: String = "",
    val supportedCategories: List<String> = emptyList()
)

data class SearchResult(
    val title: String,
    val category: String,
    val downloadUrl: String,
    val fileName: String,
    val fileSize: Long,
    val fileUrl: String,
    val siteUrl: String,
    val seeders: Int,
    val leechers: Int,
    val pubDate: String,
    val descrLink: String? = null,
    val pluginName: String
)

data class SearchQuery(
    val pattern: String,
    val category: String = "all",
    val plugins: List<String> = emptyList()
)