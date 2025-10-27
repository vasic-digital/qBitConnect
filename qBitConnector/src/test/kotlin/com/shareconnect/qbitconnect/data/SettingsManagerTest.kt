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