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