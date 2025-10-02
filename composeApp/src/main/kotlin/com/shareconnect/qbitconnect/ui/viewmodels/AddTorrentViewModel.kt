package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.qbitconnect.data.models.AddTorrentRequest
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.data.repositories.ServerRepository
import com.shareconnect.qbitconnect.data.repositories.TorrentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddTorrentViewModel(
    private val torrentRepository: TorrentRepository,
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _torrentAdded = MutableStateFlow(false)
    val torrentAdded: StateFlow<Boolean> = _torrentAdded.asStateFlow()

    // Form state
    private val _urls = MutableStateFlow("")
    val urls: StateFlow<String> = _urls.asStateFlow()

    private val _torrentFiles = MutableStateFlow<List<String>>(emptyList())
    val torrentFiles: StateFlow<List<String>> = _torrentFiles.asStateFlow()

    private val _savePath = MutableStateFlow("")
    val savePath: StateFlow<String> = _savePath.asStateFlow()

    private val _category = MutableStateFlow("")
    val category: StateFlow<String> = _category.asStateFlow()

    private val _tags = MutableStateFlow<List<String>>(emptyList())
    val tags: StateFlow<List<String>> = _tags.asStateFlow()

    private val _paused = MutableStateFlow(false)
    val paused: StateFlow<Boolean> = _paused.asStateFlow()

    private val _skipChecking = MutableStateFlow(false)
    val skipChecking: StateFlow<Boolean> = _skipChecking.asStateFlow()

    private val _downloadLimit = MutableStateFlow<Long>(-1)
    val downloadLimit: StateFlow<Long> = _downloadLimit.asStateFlow()

    private val _uploadLimit = MutableStateFlow<Long>(-1)
    val uploadLimit: StateFlow<Long> = _uploadLimit.asStateFlow()

    fun updateUrls(urls: String) {
        _urls.value = urls
    }

    fun updateTorrentFiles(files: List<String>) {
        _torrentFiles.value = files
    }

    fun updateSavePath(path: String) {
        _savePath.value = path
    }

    fun updateCategory(category: String) {
        _category.value = category
    }

    fun updateTags(tags: List<String>) {
        _tags.value = tags
    }

    fun updatePaused(paused: Boolean) {
        _paused.value = paused
    }

    fun updateSkipChecking(skip: Boolean) {
        _skipChecking.value = skip
    }

    fun updateDownloadLimit(limit: Long) {
        _downloadLimit.value = limit
    }

    fun updateUploadLimit(limit: Long) {
        _uploadLimit.value = limit
    }

    fun addTorrent() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _success.value = false

                val activeServer = serverRepository.activeServer.first()
                if (activeServer == null) {
                    _error.value = "No active server selected"
                    return@launch
                }

                if (_urls.value.isBlank() && _torrentFiles.value.isEmpty()) {
                    _error.value = "Please provide torrent URLs or select torrent files"
                    return@launch
                }

                val request = AddTorrentRequest(
                    urls = if (_urls.value.isNotBlank()) _urls.value.split("\n").filter { it.isNotBlank() } else null,
                    torrents = _torrentFiles.value.takeIf { it.isNotEmpty() },
                    savepath = _savePath.value.takeIf { it.isNotBlank() },
                    category = _category.value.takeIf { it.isNotBlank() },
                    tags = _tags.value.takeIf { it.isNotEmpty() },
                    skip_checking = _skipChecking.value,
                    paused = _paused.value,
                    downloadLimit = _downloadLimit.value.takeIf { it >= 0 },
                    uploadLimit = _uploadLimit.value.takeIf { it >= 0 }
                )

                val result = torrentRepository.addTorrent(activeServer, request)
                if (result.isSuccess) {
                    _torrentAdded.value = true
                    clearForm()
                } else {
                    _error.value = "Failed to add torrent: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Failed to add torrent: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun clearForm() {
        _urls.value = ""
        _torrentFiles.value = emptyList()
        _savePath.value = ""
        _category.value = ""
        _tags.value = emptyList()
        _paused.value = false
        _skipChecking.value = false
        _downloadLimit.value = -1
        _uploadLimit.value = -1
    }

    fun resetState() {
        _torrentAdded.value = false
        _error.value = null
    }

    fun clearError() {
        _error.value = null
    }
}