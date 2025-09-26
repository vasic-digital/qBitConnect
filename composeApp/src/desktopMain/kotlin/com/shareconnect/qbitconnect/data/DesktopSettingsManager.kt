package com.shareconnect.qbitconnect.data

import com.russhwolf.settings.Settings
import com.shareconnect.qbitconnect.model.WindowState

class DesktopSettingsManager(
    settings: Settings,
) : SettingsManager(settings) {
    val windowState = jsonPreference(settings, "windowState", WindowState())
    val language = preference(settings, "language", "")
}
