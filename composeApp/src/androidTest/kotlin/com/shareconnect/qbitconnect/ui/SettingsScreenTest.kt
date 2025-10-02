package com.shareconnect.qbitconnect.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.di.DependencyContainer
import com.shareconnect.qbitconnect.ui.viewmodels.SettingsViewModel
import com.shareconnect.qbitconnect.ui.viewmodels.SettingsViewModelFactory
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_displaysCorrectly() {
        // Initialize manual dependency injection for real SettingsManager
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        DependencyContainer.init(context)

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
    }

    @Test
    fun themeRadioButtons_exist() {
        // Initialize manual dependency injection for real SettingsManager
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        DependencyContainer.init(context)

        composeTestRule.setContent {
            val navController = rememberNavController()
            SettingsScreen(navController)
        }

        // Test that radio buttons exist (though we can't easily test selection without more setup)
        composeTestRule.onNodeWithText("System Default").assertExists()
        composeTestRule.onNodeWithText("Light").assertExists()
        composeTestRule.onNodeWithText("Dark").assertExists()
    }
}