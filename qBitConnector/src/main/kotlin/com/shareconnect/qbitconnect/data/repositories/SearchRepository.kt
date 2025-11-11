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


package com.shareconnect.qbitconnect.data.repositories

import android.util.Log
import com.shareconnect.qbitconnect.data.api.QBittorrentApiClient
import com.shareconnect.qbitconnect.data.models.SearchPlugin
import com.shareconnect.qbitconnect.data.models.SearchQuery
import com.shareconnect.qbitconnect.data.models.SearchResult
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.model.RequestResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for qBittorrent search functionality
 *
 * NOTE: This repository provides stub implementations that return success
 * without actual API calls. The underlying QBittorrentApiClient has full
 * search API implementations, but integration requires creating a SearchService
 * in the network layer following the pattern of TorrentService.
 *
 * For production use, create SearchService and integrate with RequestManager.
 */
class SearchRepository {

    private val _searchPlugins = MutableStateFlow<List<SearchPlugin>>(emptyList())
    val searchPlugins: Flow<List<SearchPlugin>> = _searchPlugins.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: Flow<List<SearchResult>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: Flow<Boolean> = _isSearching.asStateFlow()

    /**
     * Refresh/update search plugins
     * STUB: Returns success. Actual implementation in QBittorrentApiClient.updateSearchPlugins()
     */
    suspend fun refreshPlugins(serverId: Int): RequestResult<Unit> {
        Log.d(TAG, "refreshPlugins (stub): serverId=$serverId")
        return RequestResult.Success(Unit)
    }

    /**
     * Enable or disable a search plugin
     * STUB: Returns success. Actual implementation in QBittorrentApiClient.enableSearchPlugin()
     */
    suspend fun enablePlugin(serverId: Int, pluginName: String, enable: Boolean): RequestResult<Unit> {
        Log.d(TAG, "enablePlugin (stub): serverId=$serverId, plugin=$pluginName, enable=$enable")
        return RequestResult.Success(Unit)
    }

    /**
     * Install search plugin from URL
     * STUB: Returns success. Actual implementation in QBittorrentApiClient.installSearchPlugin()
     */
    suspend fun installPlugin(serverId: Int, pluginUrl: String): RequestResult<Unit> {
        Log.d(TAG, "installPlugin (stub): serverId=$serverId, url=$pluginUrl")
        return RequestResult.Success(Unit)
    }

    /**
     * Uninstall search plugin
     * STUB: Returns success. Actual implementation in QBittorrentApiClient.uninstallSearchPlugin()
     */
    suspend fun uninstallPlugin(serverId: Int, pluginName: String): RequestResult<Unit> {
        Log.d(TAG, "uninstallPlugin (stub): serverId=$serverId, plugin=$pluginName")
        return RequestResult.Success(Unit)
    }

    /**
     * Start a new search
     * STUB: Returns dummy search ID. Actual implementation in QBittorrentApiClient.startSearch()
     */
    suspend fun startSearch(serverId: Int, query: SearchQuery): RequestResult<String> {
        Log.d(TAG, "startSearch (stub): serverId=$serverId, pattern=${query.pattern}")
        _isSearching.value = true
        // Return a dummy search ID
        return RequestResult.Success("search_${System.currentTimeMillis()}")
    }

    /**
     * Stop an ongoing search
     * STUB: Returns success. Actual implementation in QBittorrentApiClient.stopSearch()
     */
    suspend fun stopSearch(serverId: Int, searchId: String): RequestResult<Unit> {
        Log.d(TAG, "stopSearch (stub): serverId=$serverId, searchId=$searchId")
        _isSearching.value = false
        return RequestResult.Success(Unit)
    }

    /**
     * Get search results
     * STUB: Returns empty list. Actual implementation in QBittorrentApiClient.getSearchResults()
     */
    suspend fun getSearchResults(serverId: Int, searchId: String): RequestResult<List<SearchResult>> {
        Log.d(TAG, "getSearchResults (stub): serverId=$serverId, searchId=$searchId")
        _isSearching.value = false
        return RequestResult.Success(emptyList())
    }

    /**
     * Delete a search job
     * STUB: Returns success. Actual implementation in QBittorrentApiClient.deleteSearch()
     */
    suspend fun deleteSearch(serverId: Int, searchId: String): RequestResult<Unit> {
        Log.d(TAG, "deleteSearch (stub): serverId=$serverId, searchId=$searchId")
        return RequestResult.Success(Unit)
    }

    companion object {
        private const val TAG = "SearchRepository"
    }

    fun getEnabledPlugins(): List<SearchPlugin> {
        return _searchPlugins.value.filter { it.enabled }
    }

    fun getPluginByName(name: String): SearchPlugin? {
        return _searchPlugins.value.find { it.name == name }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }
}