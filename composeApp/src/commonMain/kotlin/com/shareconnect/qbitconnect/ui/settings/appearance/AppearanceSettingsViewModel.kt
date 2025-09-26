package com.shareconnect.qbitconnect.ui.settings.appearance

import androidx.lifecycle.ViewModel
import com.shareconnect.qbitconnect.data.SettingsManager

class AppearanceSettingsViewModel(
    settingsManager: SettingsManager,
) : ViewModel() {
    val enableDynamicColors = settingsManager.enableDynamicColors
    val appColor = settingsManager.appColor
    val paletteStyle = settingsManager.paletteStyle
    val theme = settingsManager.theme
    val pureBlackDarkMode = settingsManager.pureBlackDarkMode
    val showRelativeTimestamps = settingsManager.showRelativeTimestamps
}
