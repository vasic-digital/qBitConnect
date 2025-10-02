package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.data.Theme
import kotlinx.coroutines.flow.Flow

class AppViewModel(
    private val settingsManager: SettingsManager
) : ViewModel() {

    val theme: Flow<Theme> = settingsManager.theme
    val enableDynamicColors: Flow<Boolean> = settingsManager.enableDynamicColors
}