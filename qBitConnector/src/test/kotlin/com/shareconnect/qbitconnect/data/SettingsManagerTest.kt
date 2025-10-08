package com.shareconnect.qbitconnect.data

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsManagerTest {

    private lateinit var testSettings: Settings
    private lateinit var settingsManager: SettingsManager
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        testScope = TestScope()
        testSettings = MapSettings()
        settingsManager = SettingsManager(testSettings)
    }

    @Test
    fun `default theme is SYSTEM_DEFAULT`() = testScope.runTest {
        val theme = settingsManager.theme.value
        assertEquals(Theme.SYSTEM_DEFAULT, theme)
    }

    @Test
    fun `default dynamic colors is true`() = testScope.runTest {
        val enableDynamicColors = settingsManager.enableDynamicColors.value
        assertEquals(true, enableDynamicColors)
    }

    @Test
    fun `setTheme updates theme correctly`() = testScope.runTest {
        settingsManager.theme.value = Theme.DARK

        val theme = settingsManager.theme.value
        assertEquals(Theme.DARK, theme)
    }

    @Test
    fun `setEnableDynamicColors updates setting correctly`() = testScope.runTest {
        settingsManager.enableDynamicColors.value = false

        val enableDynamicColors = settingsManager.enableDynamicColors.value
        assertEquals(false, enableDynamicColors)
    }
}