package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shareconnect.qbitconnect.di.DependencyContainer

class ServerListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ServerListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ServerListViewModel(DependencyContainer.serverRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}