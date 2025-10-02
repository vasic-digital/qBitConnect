package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.data.models.Torrent
import com.shareconnect.qbitconnect.data.repositories.ServerRepository
import com.shareconnect.qbitconnect.data.repositories.TorrentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TorrentListViewModel(
    private val torrentRepository: TorrentRepository,
    private val serverRepository: ServerRepository
) : ViewModel() {

    val torrents = torrentRepository.torrents
    val categories = torrentRepository.categories
    val tags = torrentRepository.tags

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedTag = MutableStateFlow<String?>(null)
    val selectedTag: StateFlow<String?> = _selectedTag.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtered torrents based on category, tag, and search
    val filteredTorrents = combine(
        torrents,
        selectedCategory,
        selectedTag,
        searchQuery
    ) { torrentList, category, tag, query ->
        torrentList.filter { torrent ->
            val matchesCategory = category == null || torrent.category == category
            val matchesTag = tag == null || torrent.tags.contains(tag)
            val matchesSearch = query.isBlank() ||
                    torrent.name.contains(query, ignoreCase = true) ||
                    torrent.hash.contains(query, ignoreCase = true)
            matchesCategory && matchesTag && matchesSearch
        }
    }

    init {
        refreshTorrents()
    }

    fun refreshTorrents() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val activeServer = serverRepository.activeServer.first()
                if (activeServer != null) {
                    val result = torrentRepository.refreshTorrents(activeServer.id.toInt())
                    if (result.isFailure) {
                        _error.value = "Failed to refresh torrents: ${result.exceptionOrNull()?.message}"
                    }
                } else {
                    _error.value = "No active server selected"
                }
            } catch (e: Exception) {
                _error.value = "Failed to refresh torrents: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun pauseTorrents(hashes: List<String>) {
        performTorrentAction(hashes) { server, hashes ->
            torrentRepository.pauseTorrents(server.id.toInt(), hashes)
        }
    }

    fun resumeTorrents(hashes: List<String>) {
        performTorrentAction(hashes) { server, hashes ->
            torrentRepository.resumeTorrents(server, hashes)
        }
    }

    fun deleteTorrents(hashes: List<String>, deleteFiles: Boolean = false) {
        performTorrentAction(hashes) { server, hashes ->
            torrentRepository.deleteTorrents(server, hashes, deleteFiles)
        }
    }

    fun setCategory(hashes: List<String>, category: String) {
        performTorrentAction(hashes) { server, hashes ->
            torrentRepository.setCategory(server, hashes, category)
        }
    }

    fun setTags(hashes: List<String>, tags: List<String>) {
        performTorrentAction(hashes) { server, hashes ->
            torrentRepository.setTags(server, hashes, tags)
        }
    }

    fun setLimits(hashes: List<String>, downloadLimit: Long? = null, uploadLimit: Long? = null) {
        performTorrentAction(hashes) { server, hashes ->
            torrentRepository.setLimits(server, hashes, downloadLimit, uploadLimit)
        }
    }

    fun setSelectedCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun setSelectedTag(tag: String?) {
        _selectedTag.value = tag
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearFilters() {
        _selectedCategory.value = null
        _selectedTag.value = null
        _searchQuery.value = ""
    }

    private fun performTorrentAction(
        hashes: List<String>,
        action: suspend (Server, List<String>) -> Result<Unit>
    ) {
        viewModelScope.launch {
            try {
                _error.value = null

                val activeServer = serverRepository.activeServer.first()
                if (activeServer != null) {
                    val result = action(activeServer, hashes)
                    if (result.isFailure) {
                        _error.value = "Action failed: ${result.exceptionOrNull()?.message}"
                    } else {
                        // Refresh torrents after successful action
                        refreshTorrents()
                    }
                } else {
                    _error.value = "No active server selected"
                }
            } catch (e: Exception) {
                _error.value = "Action failed: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}