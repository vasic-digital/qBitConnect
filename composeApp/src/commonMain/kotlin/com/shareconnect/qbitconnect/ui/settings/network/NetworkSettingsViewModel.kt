package com.shareconnect.qbitconnect.ui.settings.network

import androidx.lifecycle.ViewModel
import com.shareconnect.qbitconnect.data.SettingsManager

class NetworkSettingsViewModel(
    settingsManager: SettingsManager,
) : ViewModel() {
    val connectionTimeout = settingsManager.connectionTimeout
    var autoRefreshInterval = settingsManager.autoRefreshInterval
}
