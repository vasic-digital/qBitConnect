package com.shareconnect.qbitconnect.data.repositories

import com.shareconnect.qbitconnect.data.models.AddTorrentRequest
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.model.ServerConfig
import com.shareconnect.qbitconnect.data.models.Torrent
import com.shareconnect.qbitconnect.data.models.TorrentState
import com.shareconnect.qbitconnect.network.RequestManager
import com.shareconnect.qbitconnect.model.RequestResult
import com.shareconnect.qbitconnect.network.Response
import com.shareconnect.qbitconnect.network.TorrentService
import com.shareconnect.qbitconnect.network.catchRequestError
import com.shareconnect.qbitconnect.di.DependencyContainer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class TorrentRepository(
    private val requestManager: RequestManager = DependencyContainer.requestManager
) {

    private val _torrents = MutableStateFlow<List<Torrent>>(emptyList())
    val torrents: Flow<List<Torrent>> = _torrents.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: Flow<List<String>> = _categories.asStateFlow()

    private val _tags = MutableStateFlow<List<String>>(emptyList())
    val tags: Flow<List<String>> = _tags.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun refreshTorrents(serverId: Int): RequestResult<Unit> {
        val result = requestManager.request(serverId) { service ->
            service.getTorrents()
        }

        return when (result) {
            is RequestResult.Success -> {
                val torrentsJson = result.data ?: ""
                val torrents = parseTorrents(torrentsJson)
                _torrents.value = torrents
                RequestResult.Success(Unit)
            }
            is RequestResult.Error -> result
            else -> throw IllegalStateException("Unexpected result")
        }
    }

    suspend fun addTorrent(serverId: Int, request: AddTorrentRequest): RequestResult<Unit> {
        val result = requestManager.request(serverId) { service ->
            service.addTorrent(request.urls ?: emptyList(), request.savepath)
        }

        return when (result) {
            is RequestResult.Success -> RequestResult.Success(Unit)
            is RequestResult.Error -> result
            else -> throw IllegalStateException("Unexpected result")
        }
    }

    suspend fun pauseTorrents(serverId: Int, hashes: List<String>): RequestResult<Unit> {
        val result = requestManager.request(serverId) { service ->
            service.pauseTorrents(hashes)
        }

        return when (result) {
            is RequestResult.Success -> RequestResult.Success(Unit)
            is RequestResult.Error -> result
            else -> throw IllegalStateException("Unexpected result")
        }
    }

    suspend fun resumeTorrents(serverId: Int, hashes: List<String>): RequestResult<Unit> {
        val result = requestManager.request(serverId) { service ->
            service.resumeTorrents(hashes)
        }

        return when (result) {
            is RequestResult.Success -> RequestResult.Success(Unit)
            is RequestResult.Error -> result
            else -> throw IllegalStateException("Unexpected result")
        }
    }

    suspend fun deleteTorrents(serverId: Int, hashes: List<String>, deleteFiles: Boolean = false): RequestResult<Unit> {
        val result = requestManager.request(serverId) { service ->
            service.deleteTorrents(hashes, deleteFiles)
        }

        return when (result) {
            is RequestResult.Success -> RequestResult.Success(Unit)
            is RequestResult.Error -> result
            else -> throw IllegalStateException("Unexpected result")
        }
    }

    suspend fun setCategory(serverId: Int, hashes: List<String>, category: String): RequestResult<Unit> {
        // TODO: Implement category setting
        return RequestResult.Success(Unit)
    }

    suspend fun setTags(serverId: Int, hashes: List<String>, tags: List<String>): RequestResult<Unit> {
        // TODO: Implement tag setting
        return RequestResult.Success(Unit)
    }

    suspend fun setLimits(serverId: Int, hashes: List<String>, downloadLimit: Long? = null, uploadLimit: Long? = null): RequestResult<Unit> {
        // TODO: Implement limit setting
        return RequestResult.Success(Unit)
    }

    suspend fun getCategories(serverId: Int): RequestResult<List<String>> {
        // TODO: Implement category fetching
        return RequestResult.Success(emptyList())
    }

    suspend fun createCategory(serverId: Int, category: String, savePath: String = ""): RequestResult<Unit> {
        // TODO: Implement category creation
        return RequestResult.Success(Unit)
    }

    suspend fun deleteCategory(serverId: Int, category: String): RequestResult<Unit> {
        // TODO: Implement category deletion
        return RequestResult.Success(Unit)
    }

    fun getTorrentByHash(hash: String): Torrent? {
        return _torrents.value.find { it.hash == hash }
    }

    fun getTorrentsByCategory(category: String): List<Torrent> {
        return _torrents.value.filter { it.category == category }
    }

    fun getTorrentsByTag(tag: String): List<Torrent> {
        return _torrents.value.filter { it.tags.contains(tag) }
    }

    private fun parseTorrents(jsonString: String): List<Torrent> {
        return try {
            val jsonElement = json.parseToJsonElement(jsonString)
            if (jsonElement is JsonArray) {
                jsonElement.mapNotNull { parseTorrent(it) }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseTorrent(jsonElement: JsonElement): Torrent? {
        return try {
            if (jsonElement is JsonObject) {
                val obj = jsonElement.jsonObject
                Torrent(
                    hash = obj["hash"]?.jsonPrimitive?.content ?: "",
                    name = obj["name"]?.jsonPrimitive?.content ?: "",
                    size = obj["size"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L,
                    progress = obj["progress"]?.jsonPrimitive?.content?.toFloatOrNull() ?: 0f,
                    downloadSpeed = obj["dlspeed"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L,
                    uploadSpeed = obj["upspeed"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L,
                    downloaded = obj["downloaded"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L,
                    uploaded = obj["uploaded"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L,
                    eta = obj["eta"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L,
                    state = TorrentState.valueOf(obj["state"]?.jsonPrimitive?.content?.uppercase() ?: "UNKNOWN"),
                    category = obj["category"]?.jsonPrimitive?.content,
                    tags = obj["tags"]?.jsonPrimitive?.content?.split(",")?.map { it.trim() } ?: emptyList(),
                    ratio = obj["ratio"]?.jsonPrimitive?.content?.toFloatOrNull() ?: 0f,
                    addedOn = kotlinx.datetime.Instant.fromEpochSeconds(obj["added_on"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L),
                    completionOn = obj["completion_on"]?.jsonPrimitive?.content?.toLongOrNull()?.let { kotlinx.datetime.Instant.fromEpochSeconds(it) },
                    savePath = obj["save_path"]?.jsonPrimitive?.content ?: "",
                    contentPath = obj["content_path"]?.jsonPrimitive?.content ?: "",
                    priority = obj["priority"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0,
                    seeds = obj["num_seeds"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0,
                    seedsTotal = obj["num_complete"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0,
                    peers = obj["num_leechs"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0,
                    peersTotal = obj["num_incomplete"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0,
                    downloadLimit = obj["dl_limit"]?.jsonPrimitive?.content?.toLongOrNull() ?: -1L,
                    uploadLimit = obj["up_limit"]?.jsonPrimitive?.content?.toLongOrNull() ?: -1L,
                    timeActive = obj["time_active"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L,
                    availability = obj["availability"]?.jsonPrimitive?.content?.toFloatOrNull() ?: 0f
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}