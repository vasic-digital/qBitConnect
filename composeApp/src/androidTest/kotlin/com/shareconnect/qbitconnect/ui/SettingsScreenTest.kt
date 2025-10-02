package com.shareconnect.qbitconnect.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.data.Theme
import com.shareconnect.qbitconnect.di.appModule
import com.shareconnect.qbitconnect.di.platformModule
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest : KoinTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_displaysCorrectly() {
        // Mock dependencies
        val mockSettingsManager = mockk<SettingsManager>(relaxed = true)

        // Start Koin with test modules
        startKoin {
            modules(appModule, platformModule)
        }

        composeTestRule.setContent {
            val navController = rememberNavController()
            SettingsScreen(navController)
        }

        // Verify UI elements are displayed
        composeTestRule.onNodeWithText("Settings").assertExists()
        composeTestRule.onNodeWithText("Appearance").assertExists()
        composeTestRule.onNodeWithText("Theme").assertExists()
        composeTestRule.onNodeWithText("System Default").assertExists()
        composeTestRule.onNodeWithText("Light").assertExists()
        composeTestRule.onNodeWithText("Dark").assertExists()
        composeTestRule.onNodeWithText("Enable Dynamic Colors").assertExists()
        composeTestRule.onNodeWithText("About").assertExists()
        composeTestRule.onNodeWithText("Back").assertExists()

        // Stop Koin
        stopKoin()
    }

    @Test
    fun themeRadioButtons_exist() {
        // Mock dependencies
        val mockSettingsManager = mockk<SettingsManager>(relaxed = true)

        // Start Koin with test modules
        startKoin {
            modules(appModule, platformModule)
        }

        composeTestRule.setContent {
            val navController = rememberNavController()
            SettingsScreen(navController)
        }

        // Test that radio buttons exist (though we can't easily test selection without more setup)
        composeTestRule.onNodeWithText("System Default").assertExists()
        composeTestRule.onNodeWithText("Light").assertExists()
        composeTestRule.onNodeWithText("Dark").assertExists()

        // Stop Koin
        stopKoin()
    }
}