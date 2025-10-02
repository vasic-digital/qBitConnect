package com.shareconnect.qbitconnect.data.repositories

import com.shareconnect.qbitconnect.data.models.RSSArticle
import com.shareconnect.qbitconnect.data.models.RSSFeed
import com.shareconnect.qbitconnect.data.models.RSSRule
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.data.models.ServerConfig
import com.shareconnect.qbitconnect.network.RequestManager
import com.shareconnect.qbitconnect.model.RequestResult
import com.shareconnect.qbitconnect.network.catchRequestError
import com.shareconnect.qbitconnect.di.DependencyContainer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class RSSRepository(
    private val requestManager: RequestManager = DependencyContainer.requestManager
) {

    private val _feeds = MutableStateFlow<List<RSSFeed>>(emptyList())
    val feeds: Flow<List<RSSFeed>> = _feeds.asStateFlow()

    private val _rules = MutableStateFlow<List<RSSRule>>(emptyList())
    val rules: Flow<List<RSSRule>> = _rules.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun refreshFeeds(server: Server): Result<Unit> {
        return catchRequestError {
            val serverConfig = ServerConfig(
                id = server.id,
                name = server.name,
                url = server.host + ":" + server.port,
                username = server.username,
                password = server.password,
                useHttps = server.useHttps
            )

            val result = requestManager.request(serverConfig.id) { service ->
                service.getRSSFeeds()
            }

            when (result) {
                is RequestResult.Success -> {
                    val feedsJson = result.data
                    val feeds = parseRSSFeeds(feedsJson)
                    _feeds.value = feeds
                    Result.success(Unit)
                }
                is RequestResult.Error -> {
                    Result.failure(Exception("Failed to fetch RSS feeds"))
                }
            }
        }.fold(
            onSuccess = { it },
            onFailure = { Result.failure(it) }
        )
    }

    suspend fun addFeed(server: Server, url: String, path: String = ""): Result<Unit> {
        // TODO: Implement RSS feed addition
        return Result.success(Unit)
    }

    suspend fun removeFeed(server: Server, uid: String): Result<Unit> {
        // TODO: Implement RSS feed removal
        return Result.success(Unit)
    }

    suspend fun moveFeed(server: Server, uid: String, dest: String): Result<Unit> {
        // TODO: Implement RSS feed moving
        return Result.success(Unit)
    }

    suspend fun refreshFeed(server: Server, uid: String): Result<Unit> {
        // TODO: Implement RSS feed refresh
        return Result.success(Unit)
    }

    suspend fun markArticleAsRead(server: Server, articleId: String): Result<Unit> {
        // TODO: Implement article marking as read
        return Result.success(Unit)
    }

    suspend fun downloadTorrentFromArticle(server: Server, articleId: String, savePath: String = ""): Result<Unit> {
        // TODO: Implement torrent download from RSS article
        return Result.success(Unit)
    }

    suspend fun getRules(server: Server): Result<List<RSSRule>> {
        // TODO: Implement RSS rules fetching
        return Result.success(emptyList())
    }

    suspend fun setRule(server: Server, ruleName: String, ruleDef: RSSRule): Result<Unit> {
        // TODO: Implement RSS rule setting
        return Result.success(Unit)
    }

    suspend fun removeRule(server: Server, ruleName: String): Result<Unit> {
        // TODO: Implement RSS rule removal
        return Result.success(Unit)
    }

    fun getFeedByUid(uid: String): RSSFeed? {
        return _feeds.value.find { it.uid == uid }
    }

    fun getArticlesByFeed(uid: String): List<RSSArticle> {
        return getFeedByUid(uid)?.articles ?: emptyList()
    }

    fun getUnreadArticles(): List<RSSArticle> {
        return _feeds.value.flatMap { it.articles }.filter { !it.isRead }
    }

    private fun parseRSSFeeds(jsonString: String): List<RSSFeed> {
        return try {
            val jsonElement = json.parseToJsonElement(jsonString)
            if (jsonElement is JsonArray) {
                jsonElement.mapNotNull { parseRSSFeed(it) }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseRSSFeed(jsonElement: JsonElement): RSSFeed? {
        return try {
            if (jsonElement is kotlinx.serialization.json.JsonObject) {
                val obj = jsonElement.jsonObject
                val articlesArray = obj["articles"]?.jsonArray ?: kotlinx.serialization.json.JsonArray(emptyList())

                RSSFeed(
                    uid = obj["uid"]?.jsonPrimitive?.content ?: "",
                    title = obj["title"]?.jsonPrimitive?.content ?: "",
                    url = obj["url"]?.jsonPrimitive?.content ?: "",
                    articles = articlesArray.mapNotNull { parseRSSArticle(it) }
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseRSSArticle(jsonElement: JsonElement): RSSArticle? {
        return try {
            if (jsonElement is kotlinx.serialization.json.JsonObject) {
                val obj = jsonElement.jsonObject
                RSSArticle(
                    id = obj["id"]?.jsonPrimitive?.content ?: "",
                    title = obj["title"]?.jsonPrimitive?.content ?: "",
                    description = obj["description"]?.jsonPrimitive?.content ?: "",
                    link = obj["link"]?.jsonPrimitive?.content ?: "",
                    torrentURL = obj["torrentURL"]?.jsonPrimitive?.content,
                    date = obj["date"]?.jsonPrimitive?.content ?: "",
                    isRead = obj["isRead"]?.jsonPrimitive?.content?.toBoolean() ?: false
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}