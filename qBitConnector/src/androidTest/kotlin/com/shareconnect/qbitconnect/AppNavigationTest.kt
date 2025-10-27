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


package com.shareconnect.qbitconnect

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.qbitconnect.di.DependencyContainer
import com.shareconnect.qbitconnect.ui.App
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun app_startsAndDisplaysServerList() {
        // Initialize manual dependency injection
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        DependencyContainer.init(context)

        composeTestRule.setContent {
            App()
        }

        // Verify the app starts and shows the server list screen
        composeTestRule.onNodeWithText("qBitConnect").assertExists()
        composeTestRule.onNodeWithText("No servers configured").assertExists()
        composeTestRule.onNodeWithText("Add Server").assertExists()
    }

    @Test
    fun navigationToSettings_works() {
        // Initialize manual dependency injection
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        DependencyContainer.init(context)

        composeTestRule.setContent {
            App()
        }

        // Click on settings (represented by gear emoji)
        composeTestRule.onNodeWithText("⚙️").performClick()

        // Verify we're on the settings screen
        composeTestRule.onNodeWithText("Settings").assertExists()
        composeTestRule.onNodeWithText("Appearance").assertExists()
    }

    @Test
    fun settingsScreen_backNavigation_works() {
        // Initialize manual dependency injection
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        DependencyContainer.init(context)

        composeTestRule.setContent {
            App()
        }

        // Navigate to settings
        composeTestRule.onNodeWithText("⚙️").performClick()

        // Click back
        composeTestRule.onNodeWithText("Back").performClick()

        // Verify we're back on the server list
        composeTestRule.onNodeWithText("qBitConnect").assertExists()
        composeTestRule.onNodeWithText("No servers configured").assertExists()
    }
}