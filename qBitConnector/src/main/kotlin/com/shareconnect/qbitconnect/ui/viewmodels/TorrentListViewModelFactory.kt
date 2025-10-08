package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shareconnect.qbitconnect.di.DependencyContainer

class TorrentListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TorrentListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TorrentListViewModel(DependencyContainer.torrentRepository, DependencyContainer.serverRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}