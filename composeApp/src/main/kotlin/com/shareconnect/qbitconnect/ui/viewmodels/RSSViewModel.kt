package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.qbitconnect.data.models.RSSArticle
import com.shareconnect.qbitconnect.data.models.RSSFeed
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.data.repositories.RSSRepository
import com.shareconnect.qbitconnect.data.repositories.ServerRepository
import com.shareconnect.qbitconnect.model.RequestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RSSViewModel(
    private val rssRepository: RSSRepository,
    private val serverRepository: ServerRepository
) : ViewModel() {

    val feeds = rssRepository.feeds
    val rules = rssRepository.rules

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedFeed = MutableStateFlow<RSSFeed?>(null)
    val selectedFeed: StateFlow<RSSFeed?> = _selectedFeed.asStateFlow()

    private val _showUnreadOnly = MutableStateFlow(false)
    val showUnreadOnly: StateFlow<Boolean> = _showUnreadOnly.asStateFlow()

    // Combined articles from all feeds or selected feed
    val articles = combine(feeds, selectedFeed, showUnreadOnly) { feedList, selected, unreadOnly ->
        val allArticles = if (selected != null) {
            selected.articles
        } else {
            feedList.flatMap { it.articles }
        }

        if (unreadOnly) {
            allArticles.filter { !it.isRead }
        } else {
            allArticles
        }
    }

    init {
        refreshFeeds()
    }

    fun refreshFeeds() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val activeServer = serverRepository.activeServer.first()
                if (activeServer != null) {
                    val result = rssRepository.refreshFeeds(activeServer.id)
                    when (result) {
                        is RequestResult.Error -> {
                            _error.value = "Failed to refresh RSS feeds"
                        }
                        else -> {}
                    }
                } else {
                    _error.value = "No active server selected"
                }
            } catch (e: Exception) {
                _error.value = "Failed to refresh RSS feeds: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addFeed(url: String, path: String = "") {
        viewModelScope.launch {
            try {
                _error.value = null

                val activeServer = serverRepository.activeServer.first()
                if (activeServer != null) {
                    val result = rssRepository.addFeed(activeServer.id, url, path)
                    when (result) {
                        is RequestResult.Success -> {
                            refreshFeeds() // Refresh to get the new feed
                        }
                        else -> {
                            _error.value = "Failed to add RSS feed"
                        }
                    }
                } else {
                    _error.value = "No active server selected"
                }
            } catch (e: Exception) {
                _error.value = "Failed to add RSS feed: ${e.message}"
            }
        }
    }

    fun removeFeed(feedUid: String) {
        viewModelScope.launch {
            try {
                _error.value = null

                val activeServer = serverRepository.activeServer.first()
                if (activeServer != null) {
                    val result = rssRepository.removeFeed(activeServer.id, feedUid)
                    when (result) {
                        is RequestResult.Success -> {
                            if (_selectedFeed.value?.uid == feedUid) {
                                _selectedFeed.value = null
                            }
                            refreshFeeds()
                        }
                        else -> {
                            _error.value = "Failed to remove RSS feed"
                        }
                    }
                } else {
                    _error.value = "No active server selected"
                }
            } catch (e: Exception) {
                _error.value = "Failed to remove RSS feed: ${e.message}"
            }
        }
    }

    fun refreshFeed(feedUid: String) {
        viewModelScope.launch {
            try {
                _error.value = null

                val activeServer = serverRepository.activeServer.first()
                if (activeServer != null) {
                    val result = rssRepository.refreshFeed(activeServer.id, feedUid)
                    when (result) {
                        is RequestResult.Success -> {
                            refreshFeeds()
                        }
                        else -> {
                            _error.value = "Failed to refresh RSS feed"
                        }
                    }
                } else {
                    _error.value = "No active server selected"
                }
            } catch (e: Exception) {
                _error.value = "Failed to refresh RSS feed: ${e.message}"
            }
        }
    }

    fun markArticleAsRead(articleId: String) {
        viewModelScope.launch {
            try {
                val activeServer = serverRepository.activeServer.first()
                if (activeServer != null) {
                    rssRepository.markArticleAsRead(activeServer.id, articleId)
                }
            } catch (e: Exception) {
                _error.value = "Failed to mark article as read: ${e.message}"
            }
        }
    }

    fun downloadTorrentFromArticle(articleId: String, savePath: String = "") {
        viewModelScope.launch {
            try {
                _error.value = null

                val activeServer = serverRepository.activeServer.first()
                if (activeServer != null) {
                    val result = rssRepository.downloadTorrentFromArticle(activeServer.id, articleId, savePath)
                    when (result) {
                        is RequestResult.Success -> {}
                        else -> {
                            _error.value = "Failed to download torrent"
                        }
                    }
                } else {
                    _error.value = "No active server selected"
                }
            } catch (e: Exception) {
                _error.value = "Failed to download torrent: ${e.message}"
            }
        }
    }

    fun selectFeed(feed: RSSFeed?) {
        _selectedFeed.value = feed
    }

    fun toggleShowUnreadOnly() {
        _showUnreadOnly.value = !_showUnreadOnly.value
    }

    fun clearError() {
        _error.value = null
    }
}