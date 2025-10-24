package com.shareconnect.qbitconnect.ui.viewmodels

import android.app.Application
import com.shareconnect.qbitconnect.App
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.data.Theme
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var testSettings: Settings
    private lateinit var settingsManager: SettingsManager
    private lateinit var viewModel: SettingsViewModel
    private lateinit var mockApplication: App

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setTheme updates theme correctly`() = runTest {
        testSettings = MapSettings()
        settingsManager = SettingsManager(testSettings)
        mockApplication = mockk<App>(relaxed = true)
        viewModel = SettingsViewModel(settingsManager, mockApplication)

        val theme = Theme.DARK

        viewModel.setTheme(theme)

        assertEquals(theme, settingsManager.theme.value)
    }

    @Test
    fun `setEnableDynamicColors updates setting correctly`() = runTest {
        testSettings = MapSettings()
        settingsManager = SettingsManager(testSettings)
        mockApplication = mockk<App>(relaxed = true)
        viewModel = SettingsViewModel(settingsManager, mockApplication)

        val enabled = false

        viewModel.setEnableDynamicColors(enabled)

        assertEquals(enabled, settingsManager.enableDynamicColors.value)
    }
}