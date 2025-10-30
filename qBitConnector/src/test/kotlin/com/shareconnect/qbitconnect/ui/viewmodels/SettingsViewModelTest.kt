/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = com.shareconnect.qbitconnect.TestApplication::class)
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