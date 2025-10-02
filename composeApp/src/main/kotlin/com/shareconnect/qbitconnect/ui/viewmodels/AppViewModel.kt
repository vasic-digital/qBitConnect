package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.data.repositories.ServerRepository

class AppViewModel(
    private val serverRepository: ServerRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    val theme = settingsManager.theme.flow
    val enableDynamicColors = settingsManager.enableDynamicColors.flow
}