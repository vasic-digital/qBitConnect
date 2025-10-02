package com.shareconnect.qbitconnect

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.qbitconnect.ui.App
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Automation test to verify that the application loads and starts
 * and presents the main screen without crashing.
 */
@RunWith(AndroidJUnit4::class)
class AppLaunchTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun app_launches_without_crashing() {
        // Get the application context
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertNotNull("Application context should not be null", context)

        // Set up the composable content
        composeTestRule.setContent {
            App()
        }

        // Wait for the app to compose and verify it doesn't crash
        // The test passes if we reach this point without exceptions
        composeTestRule.waitForIdle()

        // Verify that the app has loaded by checking for the main screen test tag
        // This ensures the main screen is displayed and the app didn't crash
        composeTestRule.onNodeWithTag("main_screen").assertExists()

        // If we reach this point, the app launched successfully without crashing
    }
}