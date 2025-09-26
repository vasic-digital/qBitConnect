package com.shareconnect.qbitconnect.ui.settings.general

import androidx.lifecycle.ViewModel
import com.shareconnect.qbitconnect.data.SettingsManager

class GeneralSettingsViewModel(
    settingsManager: SettingsManager,
) : ViewModel() {
    var notificationCheckInterval = settingsManager.notificationCheckInterval
    var areTorrentSwipeActionsEnabled = settingsManager.areTorrentSwipeActionsEnabled
    val checkUpdates = settingsManager.checkUpdates
}
