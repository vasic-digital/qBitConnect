package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shareconnect.qbitconnect.data.repositories.ServerRepository
import com.shareconnect.qbitconnect.data.repositories.TorrentRepository

class TorrentListViewModelFactory(
    private val torrentRepository: TorrentRepository,
    private val serverRepository: ServerRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TorrentListViewModel::class.java)) {
            return TorrentListViewModel(torrentRepository, serverRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}