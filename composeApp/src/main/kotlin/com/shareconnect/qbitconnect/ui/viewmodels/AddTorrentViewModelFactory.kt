package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shareconnect.qbitconnect.di.DependencyContainer

class AddTorrentViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTorrentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddTorrentViewModel(DependencyContainer.torrentRepository, DependencyContainer.serverRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}