package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.data.Theme
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsManager: SettingsManager
) : ViewModel() {

    val theme = settingsManager.theme.flow
    val enableDynamicColors = settingsManager.enableDynamicColors.flow

    fun setTheme(theme: Theme) {
        settingsManager.theme.value = theme
    }

    fun setEnableDynamicColors(enabled: Boolean) {
        settingsManager.enableDynamicColors.value = enabled
    }
}