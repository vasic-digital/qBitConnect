package com.shareconnect.qbitconnect.ui.viewmodels

import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.data.Theme
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val mockSettingsManager = mockk<SettingsManager>(relaxed = true)
    private val viewModel = SettingsViewModel(mockSettingsManager)

    @Test
    fun `setTheme calls SettingsManager setTheme`() = runTest {
        val theme = Theme.DARK

        viewModel.setTheme(theme)

        coVerify { mockSettingsManager.setTheme(theme) }
    }

    @Test
    fun `setEnableDynamicColors calls SettingsManager setEnableDynamicColors`() = runTest {
        val enabled = false

        viewModel.setEnableDynamicColors(enabled)

        coVerify { mockSettingsManager.setEnableDynamicColors(enabled) }
    }
}