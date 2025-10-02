package com.shareconnect.qbitconnect.di

import com.russhwolf.settings.MapSettings
import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.data.SettingsManager
import org.junit.Assert.assertNotNull
import org.junit.Test

class DependencyContainerTest {

    @Test
    fun `SharedDependencyContainer should initialize correctly`() {
        // Mock dependencies
        val settings = MapSettings()
        val settingsManager = SettingsManager(settings)
        val serverManager = ServerManager(settings)

        SharedDependencyContainer.init(settingsManager, serverManager)

        assertNotNull(SharedDependencyContainer.settingsManager)
        assertNotNull(SharedDependencyContainer.serverManager)
    }
}