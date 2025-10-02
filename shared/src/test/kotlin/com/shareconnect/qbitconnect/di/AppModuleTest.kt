package com.shareconnect.qbitconnect.di

import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.data.SettingsManager
import org.junit.Assert.assertNotNull
import org.junit.Test

class DependencyContainerTest {

    @Test
    fun `SharedDependencyContainer should initialize correctly`() {
        // Mock dependencies
        val settingsManager = SettingsManager(null) // Mock or null for test
        val serverManager = ServerManager(null)

        SharedDependencyContainer.init(settingsManager, serverManager)

        assertNotNull(SharedDependencyContainer.settingsManager)
        assertNotNull(SharedDependencyContainer.serverManager)
    }
}