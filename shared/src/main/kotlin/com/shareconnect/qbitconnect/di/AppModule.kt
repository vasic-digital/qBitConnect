package com.shareconnect.qbitconnect.di

import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.data.SettingsManager

// Manual dependency injection for shared module
object SharedDependencyContainer {
    // These will be initialized from platform-specific code
    lateinit var settingsManager: SettingsManager
    lateinit var serverManager: ServerManager

    fun init(settingsManager: SettingsManager, serverManager: ServerManager) {
        this.settingsManager = settingsManager
        this.serverManager = serverManager
    }
}