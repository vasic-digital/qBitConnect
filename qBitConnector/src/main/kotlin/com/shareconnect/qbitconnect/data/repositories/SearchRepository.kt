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

import com.shareconnect.qbitconnect.data.models.SearchPlugin
import com.shareconnect.qbitconnect.data.models.SearchQuery
import com.shareconnect.qbitconnect.data.models.SearchResult
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.model.RequestResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchRepository {

    private val _searchPlugins = MutableStateFlow<List<SearchPlugin>>(emptyList())
    val searchPlugins: Flow<List<SearchPlugin>> = _searchPlugins.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: Flow<List<SearchResult>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: Flow<Boolean> = _isSearching.asStateFlow()

    suspend fun refreshPlugins(serverId: Int): RequestResult<Unit> {
        // TODO: Implement actual API call to refresh search plugins
        return RequestResult.Success(Unit)
    }

    suspend fun enablePlugin(serverId: Int, pluginName: String, enable: Boolean): RequestResult<Unit> {
        // TODO: Implement actual API call to enable/disable search plugin
        return RequestResult.Success(Unit)
    }

    suspend fun installPlugin(serverId: Int, pluginUrl: String): RequestResult<Unit> {
        // TODO: Implement actual API call to install search plugin
        return RequestResult.Success(Unit)
    }

    suspend fun uninstallPlugin(serverId: Int, pluginName: String): RequestResult<Unit> {
        // TODO: Implement actual API call to uninstall search plugin
        return RequestResult.Success(Unit)
    }

    suspend fun startSearch(serverId: Int, query: SearchQuery): RequestResult<String> {
        // TODO: Implement actual API call to start search
        _isSearching.value = true
        // Return a search ID
        return RequestResult.Success("search_123")
    }

    suspend fun stopSearch(serverId: Int, searchId: String): RequestResult<Unit> {
        // TODO: Implement actual API call to stop search
        _isSearching.value = false
        return RequestResult.Success(Unit)
    }

    suspend fun getSearchResults(serverId: Int, searchId: String): RequestResult<List<SearchResult>> {
        // TODO: Implement actual API call to get search results
        _isSearching.value = false
        return RequestResult.Success(emptyList())
    }

    suspend fun deleteSearch(serverId: Int, searchId: String): RequestResult<Unit> {
        // TODO: Implement actual API call to delete search
        return RequestResult.Success(Unit)
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