package com.shareconnect.qbitconnect

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.qbitconnect.di.appModule
import com.shareconnect.qbitconnect.di.platformModule
import com.shareconnect.qbitconnect.ui.App
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class AppNavigationTest : KoinTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun app_startsAndDisplaysServerList() {
        // Start Koin
        startKoin {
            androidContext(androidContext)
            modules(appModule, platformModule)
        }

        composeTestRule.setContent {
            App()
        }

        // Verify the app starts and shows the server list screen
        composeTestRule.onNodeWithText("qBitConnect").assertExists()
        composeTestRule.onNodeWithText("No servers configured").assertExists()
        composeTestRule.onNodeWithText("Add Server").assertExists()

        // Stop Koin
        stopKoin()
    }

    @Test
    fun navigationToSettings_works() {
        // Start Koin
        startKoin {
            androidContext(androidContext)
            modules(appModule, platformModule)
        }

        composeTestRule.setContent {
            App()
        }

        // Click on settings (represented by gear emoji)
        composeTestRule.onNodeWithText("⚙️").performClick()

        // Verify we're on the settings screen
        composeTestRule.onNodeWithText("Settings").assertExists()
        composeTestRule.onNodeWithText("Appearance").assertExists()

        // Stop Koin
        stopKoin()
    }

    @Test
    fun settingsScreen_backNavigation_works() {
        // Start Koin
        startKoin {
            androidContext(androidContext)
            modules(appModule, platformModule)
        }

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

        // Stop Koin
        stopKoin()
    }
}