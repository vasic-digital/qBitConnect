package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.data.Theme
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsManager: SettingsManager
) : ViewModel() {

    val theme = settingsManager.theme
    val enableDynamicColors = settingsManager.enableDynamicColors

    fun setTheme(theme: Theme) {
        viewModelScope.launch {
            settingsManager.setTheme(theme)
        }
    }

    fun setEnableDynamicColors(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setEnableDynamicColors(enabled)
        }
    }
}