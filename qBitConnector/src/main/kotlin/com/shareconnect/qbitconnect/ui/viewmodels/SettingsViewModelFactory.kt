package com.shareconnect.qbitconnect.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shareconnect.qbitconnect.di.DependencyContainer

class SettingsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(DependencyContainer.settingsManager, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}