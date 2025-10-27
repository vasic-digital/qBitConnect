/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.shareconnect.qbitconnect.data.repositories

import com.shareconnect.qbitconnect.data.models.RSSArticle
import com.shareconnect.qbitconnect.data.models.RSSFeed
import com.shareconnect.qbitconnect.data.models.RSSRule
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.model.ServerConfig
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

    suspend fun refreshFeeds(serverId: Int): RequestResult<Unit> {
        val result = requestManager.request(serverId) { service ->
            service.getRSSFeeds()
        }

        return when (result) {
            is RequestResult.Success -> {
                val feedsJson = result.data ?: ""
                val feeds = parseRSSFeeds(feedsJson)
                _feeds.value = feeds
                RequestResult.Success(Unit)
            }
            is RequestResult.Error -> result
            else -> throw IllegalStateException("Unexpected result")
        }
    }

    suspend fun addFeed(serverId: Int, url: String, path: String = ""): RequestResult<Unit> {
        // TODO: Implement RSS feed addition
        return RequestResult.Success(Unit)
    }

    suspend fun removeFeed(serverId: Int, uid: String): RequestResult<Unit> {
        // TODO: Implement RSS feed removal
        return RequestResult.Success(Unit)
    }

    suspend fun moveFeed(serverId: Int, uid: String, dest: String): RequestResult<Unit> {
        // TODO: Implement RSS feed moving
        return RequestResult.Success(Unit)
    }

    suspend fun refreshFeed(serverId: Int, uid: String): RequestResult<Unit> {
        // TODO: Implement RSS feed refresh
        return RequestResult.Success(Unit)
    }

    suspend fun markArticleAsRead(serverId: Int, articleId: String): RequestResult<Unit> {
        // TODO: Implement article marking as read
        return RequestResult.Success(Unit)
    }

    suspend fun downloadTorrentFromArticle(serverId: Int, articleId: String, savePath: String = ""): RequestResult<Unit> {
        // TODO: Implement torrent download from RSS article
        return RequestResult.Success(Unit)
    }

    suspend fun getRules(serverId: Int): RequestResult<List<RSSRule>> {
        // TODO: Implement RSS rules fetching
        return RequestResult.Success(emptyList())
    }

    suspend fun setRule(serverId: Int, ruleName: String, ruleDef: RSSRule): RequestResult<Unit> {
        // TODO: Implement RSS rule setting
        return RequestResult.Success(Unit)
    }

    suspend fun removeRule(serverId: Int, ruleName: String): RequestResult<Unit> {
        // TODO: Implement RSS rule removal
        return RequestResult.Success(Unit)
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
                    description = obj["description"]?.jsonPrimitive?.content,
                    link = obj["link"]?.jsonPrimitive?.content ?: "",
                    author = obj["author"]?.jsonPrimitive?.content,
                    category = obj["category"]?.jsonPrimitive?.content,
                    pubDate = obj["date"]?.jsonPrimitive?.content?.let { kotlinx.datetime.Instant.parse(it) },
                    isRead = obj["isRead"]?.jsonPrimitive?.content?.toBoolean() ?: false,
                    torrentUrl = obj["torrentURL"]?.jsonPrimitive?.content,
                    size = obj["size"]?.jsonPrimitive?.content?.toLongOrNull()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}