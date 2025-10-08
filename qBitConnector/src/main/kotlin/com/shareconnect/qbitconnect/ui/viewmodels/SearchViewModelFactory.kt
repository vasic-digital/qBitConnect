package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shareconnect.qbitconnect.di.DependencyContainer

class SearchViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(DependencyContainer.searchRepository, DependencyContainer.serverRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}