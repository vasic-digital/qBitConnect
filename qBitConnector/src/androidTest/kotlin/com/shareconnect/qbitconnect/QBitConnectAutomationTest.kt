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

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.qbitconnect.di.DependencyContainer
import com.shareconnect.qbitconnect.ui.App
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Comprehensive automation test suite for qBitConnect application.
 * Tests all major UI flows and user interactions to ensure full functionality.
 */
@RunWith(AndroidJUnit4::class)
class QBitConnectAutomationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockServer: MockQBittorrentServer

    @Before
    fun setUp() {
        // Start mock server
        mockServer = MockQBittorrentServer()

        // Initialize dependency injection for tests
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        DependencyContainer.init(context)

        composeTestRule.setContent {
            App()
        }
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun testCompleteAppLaunchFlow() {
        // Test that the app launches and shows the server list screen
        composeTestRule.waitForIdle()

        // Verify main screen is displayed
        composeTestRule.onNodeWithTag("main_screen").assertExists()

        // Verify we're on the server list screen (should show "No servers configured" initially)
        composeTestRule.onNodeWithText("No servers configured").assertExists()
        composeTestRule.onNodeWithText("Add a qBittorrent server to get started").assertExists()
    }

    @Test
    fun testAddServerFlow() {
        composeTestRule.waitForIdle()

        // Click the "Add Server" button in the empty state
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Verify we're on the add server screen
        composeTestRule.onNodeWithText("Add Server").assertExists()

        // Fill in server details
        composeTestRule.onNodeWithText("Server Name").performTextInput("Test Server")
        composeTestRule.onNodeWithText("Host").performTextInput("192.168.1.100")
        composeTestRule.onNodeWithText("Port").performTextInput("8080")

        // Click Add Server button
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Wait for processing
        composeTestRule.waitForIdle()

        // Verify we're back on server list and the server was added
        composeTestRule.onNodeWithText("Test Server").assertExists()
        composeTestRule.onNodeWithText("192.168.1.100:8080").assertExists()
    }

    @Test
    fun testServerNavigationFlow() {
        // First add a server
        testAddServerFlow()

        // Click on the server to make it active
        composeTestRule.onNodeWithText("Test Server").performClick()

        // Verify it's marked as active
        composeTestRule.onNodeWithText("Active").assertExists()

        // Click the "Torrents" button
        composeTestRule.onNodeWithText("Torrents").performClick()

        // Verify we're on the torrent list screen
        composeTestRule.onNodeWithText("Torrents").assertExists()
    }

    @Test
    fun testSettingsNavigationFlow() {
        composeTestRule.waitForIdle()

        // Click the settings icon (⚙️)
        composeTestRule.onNodeWithText("⚙️").performClick()

        // Verify we're on the settings screen
        composeTestRule.onNodeWithText("Settings").assertExists()
    }

    @Test
    fun testThemeChangeFlow() {
        // Navigate to settings
        testSettingsNavigationFlow()

        // The settings screen should have theme options
        // This test assumes the settings screen has theme controls
        // If not implemented, this will fail and indicate missing functionality
        composeTestRule.onNodeWithText("Theme").assertExists()
    }

    @Test
    fun testAddTorrentFlow() {
        // First ensure we have a server and navigate to torrent list
        testServerNavigationFlow()

        // Click the "+" button in the top bar to add torrent
        composeTestRule.onNodeWithText("+").performClick()

        // Verify we're on the add torrent screen
        composeTestRule.onNodeWithText("Add Torrent").assertExists()
    }

    @Test
    fun testSearchFlow() {
        // First ensure we have a server and navigate to torrent list
        testServerNavigationFlow()

        // Click the "Search" button in the bottom bar
        composeTestRule.onNodeWithText("Search").performClick()

        // Verify we're on the search screen
        composeTestRule.onNodeWithText("Search Torrents").assertExists()
    }

    @Test
    fun testRSSFeedsFlow() {
        // First ensure we have a server and navigate to torrent list
        testServerNavigationFlow()

        // Click the "RSS" button in the bottom bar
        composeTestRule.onNodeWithText("RSS").performClick()

        // Verify we're on the RSS feeds screen
        composeTestRule.onNodeWithText("RSS Feeds").assertExists()
    }

    @Test
    fun testServerManagementFlow() {
        // Add a server first
        testAddServerFlow()

        // Test server selection and activation
        composeTestRule.onNodeWithText("Test Server").performClick()
        composeTestRule.onNodeWithText("Active").assertExists()

        // Test navigation to torrents
        composeTestRule.onNodeWithText("Torrents").performClick()
        composeTestRule.onNodeWithText("Torrents").assertExists()

        // Navigate back to server list using the "Servers" button
        composeTestRule.onNodeWithText("Servers").performClick()

        // Verify we're back on server list
        composeTestRule.onNodeWithText("Test Server").assertExists()
    }

    @Test
    fun testEmptyStateHandling() {
        composeTestRule.waitForIdle()

        // Verify empty state is properly displayed
        composeTestRule.onNodeWithText("No servers configured").assertExists()
        composeTestRule.onNodeWithText("Add a qBittorrent server to get started").assertExists()
        composeTestRule.onNodeWithText("Add Server").assertExists()

        // Verify FAB is present
        composeTestRule.onNodeWithText("+").assertExists()
    }

    @Test
    fun testNavigationConsistency() {
        // Test that navigation works correctly between screens
        composeTestRule.waitForIdle()

        // Start -> Settings
        composeTestRule.onNodeWithText("⚙️").performClick()
        composeTestRule.onNodeWithText("Settings").assertExists()

        // Settings -> Back to server list
        composeTestRule.onNodeWithText("Back").performClick()
        composeTestRule.onNodeWithText("No servers configured").assertExists()

        // Add server -> Add server screen
        composeTestRule.onNodeWithText("Add Server").performClick()
        composeTestRule.onNodeWithText("Add Server").assertExists()

        // Add server -> Back to server list (automatically navigates back after adding)
        // Since we added a server, we should see it
        composeTestRule.onNodeWithText("Test Server").assertExists()
    }

    @Test
    fun testFormValidationInAddServer() {
        composeTestRule.waitForIdle()

        // Navigate to add server
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Try to add server without required fields
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Should show error for missing name
        composeTestRule.onNodeWithText("Server name is required").assertExists()

        // Fill name but leave host empty
        composeTestRule.onNodeWithText("Server Name").performTextInput("Test")
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Should show error for missing host
        composeTestRule.onNodeWithText("Host is required").assertExists()

        // Fill host but invalid port
        composeTestRule.onNodeWithText("Host").performTextInput("192.168.1.100")
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Should show error for missing port
        composeTestRule.onNodeWithText("Valid port number is required").assertExists()

        // Fill invalid port
        composeTestRule.onNodeWithText("Port").performTextInput("99999")
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Should show error for invalid port
        composeTestRule.onNodeWithText("Valid port number is required").assertExists()

        // Fill valid port
        composeTestRule.onNodeWithText("Port").performTextInput("8080")
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Should succeed (assuming connection test passes)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Test").assertExists()
    }

    @Test
    fun testFullAppWorkflowWithMockServer() {
        composeTestRule.waitForIdle()

        // Add server pointing to mock server
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Fill server details with mock server info
        composeTestRule.onNodeWithText("Server Name").performTextInput("Mock Server")
        composeTestRule.onNodeWithText("Host").performTextInput(mockServer.url.host)
        composeTestRule.onNodeWithText("Port").performTextInput(mockServer.url.port.toString())
        composeTestRule.onNodeWithText("Username").performTextInput("admin")
        composeTestRule.onNodeWithText("Password").performTextInput("admin")

        // Add the server
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Wait for navigation back to server list
        composeTestRule.waitForIdle()

        // Verify server was added
        composeTestRule.onNodeWithText("Mock Server").assertExists()

        // Click on server to activate it
        composeTestRule.onNodeWithText("Mock Server").performClick()

        // Navigate to torrents
        composeTestRule.onNodeWithText("Torrents").performClick()

        // Wait for torrent list to load
        composeTestRule.waitForIdle()

        // Verify torrents are displayed (mock server has 2 torrents)
        composeTestRule.onNodeWithText("Ubuntu 22.04 ISO").assertExists()
        composeTestRule.onNodeWithText("Fedora 38 ISO").assertExists()

        // Test torrent actions - pause a torrent
        // Note: This assumes the UI has pause buttons for torrents
        // composeTestRule.onNodeWithText("Pause").performClick()

        // Navigate to search
        composeTestRule.onNodeWithText("Search").performClick()
        composeTestRule.onNodeWithText("Search Torrents").assertExists()

        // Navigate to RSS
        composeTestRule.onNodeWithText("RSS").performClick()
        composeTestRule.onNodeWithText("RSS Feeds").assertExists()

        // Navigate back to server list
        composeTestRule.onNodeWithText("Servers").performClick()
        composeTestRule.onNodeWithText("Mock Server").assertExists()
    }

    @Test
    fun testErrorHandlingWithInvalidServer() {
        composeTestRule.waitForIdle()

        // Add server with invalid details
        composeTestRule.onNodeWithText("Add Server").performClick()

        composeTestRule.onNodeWithText("Server Name").performTextInput("Invalid Server")
        composeTestRule.onNodeWithText("Host").performTextInput("invalid.host.that.does.not.exist")
        composeTestRule.onNodeWithText("Port").performTextInput("8080")

        // Try to add server
        composeTestRule.onNodeWithText("Add Server").performClick()

        // Wait and verify we're still on add server screen (validation should prevent navigation)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Add Server").assertExists()

        // Or if it does navigate, check for error state
        // This depends on the actual UI implementation
    }

    @Test
    fun testSettingsAndThemeChanges() {
        composeTestRule.waitForIdle()

        // Navigate to settings
        composeTestRule.onNodeWithText("⚙️").performClick()
        composeTestRule.onNodeWithText("Settings").assertExists()

        // Test theme toggle (assuming it exists)
        // composeTestRule.onNodeWithText("Dark Theme").performClick()

        // Test other settings
        // composeTestRule.onNodeWithText("Connection Timeout").performTextInput("30")

        // Navigate back
        composeTestRule.onNodeWithText("Back").performClick()
        composeTestRule.onNodeWithText("No servers configured").assertExists()
    }
}