package com.shareconnect.qbitconnect.data.models

data class SearchPlugin(
    val name: String,
    val fullName: String,
    val url: String,
    val enabled: Boolean = true,
    val version: String = "",
    val supportedCategories: List<String> = emptyList()
)

data class SearchQuery(
    val pattern: String,
    val category: String = "all",
    val plugins: List<String> = emptyList()
)

data class SearchResult(
    val fileName: String,
    val fileSize: Long,
    val nbSeeders: Int,
    val nbLeechers: Int,
    val fileUrl: String,
    val descrLink: String = "",
    val engineName: String = ""
)