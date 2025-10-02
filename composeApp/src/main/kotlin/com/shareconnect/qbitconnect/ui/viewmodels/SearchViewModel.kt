package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.qbitconnect.data.models.SearchQuery
import com.shareconnect.qbitconnect.data.models.SearchResult
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.data.repositories.SearchRepository
import com.shareconnect.qbitconnect.data.repositories.ServerRepository
import com.shareconnect.qbitconnect.model.RequestResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val serverRepository: ServerRepository
) : ViewModel() {

    val searchPlugins = searchRepository.searchPlugins
    val searchResults = searchRepository.searchResults
    val isSearching = searchRepository.isSearching

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _currentSearchId = MutableStateFlow<String?>(null)
    val currentSearchId: StateFlow<String?> = _currentSearchId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("all")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedPlugins = MutableStateFlow<List<String>>(emptyList())
    val selectedPlugins: StateFlow<List<String>> = _selectedPlugins.asStateFlow()

    private var searchJob: Job? = null

    fun refreshPlugins(serverId: Int) {
        viewModelScope.launch {
            try {
                _error.value = null

                val result = searchRepository.refreshPlugins(serverId)
                when (result) {
                    is RequestResult.Error -> {
                        _error.value = "Failed to refresh search plugins"
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _error.value = "Failed to refresh search plugins: ${e.message}"
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSelectedPlugins(plugins: List<String>) {
        _selectedPlugins.value = plugins
    }

    fun performSearch(serverId: Int) {
        if (_searchQuery.value.isBlank()) {
            _error.value = "Please enter a search query"
            return
        }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            try {
                _error.value = null
                searchRepository.clearSearchResults()

                val query = SearchQuery(
                    pattern = _searchQuery.value,
                    category = _selectedCategory.value,
                    plugins = _selectedPlugins.value.takeIf { it.isNotEmpty() } ?: emptyList()
                )

                val result = searchRepository.startSearch(serverId, query)
                when (result) {
                    is RequestResult.Success -> {
                        _currentSearchId.value = result.data

                        // Get search results
                        val resultsResult = searchRepository.getSearchResults(serverId, result.data)
                        when (resultsResult) {
                            is RequestResult.Success -> {
                                // Results are updated in repository
                            }
                            is RequestResult.Error -> {
                                _error.value = "Failed to get search results"
                            }
                        }
                    }
                    is RequestResult.Error -> {
                        _error.value = "Failed to start search"
                    }
                }
            } catch (e: Exception) {
                _error.value = "Search failed: ${e.message}"
            }
        }
    }

    fun stopSearch() {
        searchJob?.cancel()
        _currentSearchId.value?.let { searchId ->
            viewModelScope.launch {
                try {
                    val activeServer = serverRepository.activeServer.first()
                    if (activeServer != null) {
                        val result = searchRepository.stopSearch(activeServer.id, searchId)
                        when (result) {
                            is RequestResult.Error -> {
                                _error.value = "Failed to stop search"
                            }
                            else -> {}
                        }
                    }
                } catch (e: Exception) {
                    _error.value = "Failed to stop search: ${e.message}"
                }
            }
        }
        _currentSearchId.value = null
    }

    fun downloadTorrent(result: SearchResult) {
        viewModelScope.launch {
            try {
                _error.value = null

                val activeServer = serverRepository.activeServer.first()
                if (activeServer != null) {
                    // TODO: Implement torrent download from search result
                    // This would typically call the torrent repository to add the torrent
                    _error.value = "Torrent download from search not yet implemented"
                } else {
                    _error.value = "No active server selected"
                }
            } catch (e: Exception) {
                _error.value = "Failed to download torrent: ${e.message}"
            }
        }
    }

    fun enablePlugin(pluginName: String, enable: Boolean) {
        viewModelScope.launch {
            try {
                val activeServer = serverRepository.activeServer.first()
                if (activeServer != null) {
                    val result = searchRepository.enablePlugin(activeServer.id, pluginName, enable)
                    when (result) {
                        is RequestResult.Success -> {
                            refreshPlugins(activeServer.id)
                        }
                        is RequestResult.Error -> {
                            _error.value = "Failed to ${if (enable) "enable" else "disable"} plugin"
                        }
                    }
                } else {
                    _error.value = "No active server selected"
                }
            } catch (e: Exception) {
                _error.value = "Failed to ${if (enable) "enable" else "disable"} plugin: ${e.message}"
            }
        }
    }

    fun clearSearchResults() {
        searchRepository.clearSearchResults()
        _currentSearchId.value = null
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        stopSearch()
    }
}