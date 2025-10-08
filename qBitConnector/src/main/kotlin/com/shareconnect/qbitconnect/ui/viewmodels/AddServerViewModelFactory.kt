package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shareconnect.qbitconnect.di.DependencyContainer

class AddServerViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddServerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddServerViewModel(DependencyContainer.serverManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}