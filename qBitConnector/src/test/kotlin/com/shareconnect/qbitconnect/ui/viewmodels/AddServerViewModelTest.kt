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

import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.model.ServerConfig
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddServerViewModelTest {

    private lateinit var serverManager: ServerManager
    private lateinit var viewModel: AddServerViewModel
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        serverManager = mockk()
        coEvery { serverManager.serversFlow } returns MutableStateFlow(emptyList())
        coEvery { serverManager.addServer(any()) } returns Unit

        viewModel = AddServerViewModel(serverManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have default values`() {
        assertEquals("", viewModel.name.value)
        assertEquals("", viewModel.host.value)
        assertEquals("8080", viewModel.port.value)
        assertNull(viewModel.username.value)
        assertNull(viewModel.password.value)
        assertFalse(viewModel.useHttps.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
        assertFalse(viewModel.serverAdded.value)
    }

    @Test
    fun `updateName should update name state`() {
        val newName = "Test Server"
        viewModel.updateName(newName)
        assertEquals(newName, viewModel.name.value)
    }

    @Test
    fun `updateHost should update host state`() {
        val newHost = "192.168.1.100"
        viewModel.updateHost(newHost)
        assertEquals(newHost, viewModel.host.value)
    }

    @Test
    fun `updatePort should update port state`() {
        val newPort = "9090"
        viewModel.updatePort(newPort)
        assertEquals(newPort, viewModel.port.value)
    }

    @Test
    fun `updateUsername should update username state`() {
        val newUsername = "admin"
        viewModel.updateUsername(newUsername)
        assertEquals(newUsername, viewModel.username.value)
    }

    @Test
    fun `updatePassword should update password state`() {
        val newPassword = "password123"
        viewModel.updatePassword(newPassword)
        assertEquals(newPassword, viewModel.password.value)
    }

    @Test
    fun `updateUseHttps should update useHttps state`() {
        viewModel.updateUseHttps(true)
        assertTrue(viewModel.useHttps.value)

        viewModel.updateUseHttps(false)
        assertFalse(viewModel.useHttps.value)
    }

    @Test
    fun `addServer should create HTTP server config when HTTPS disabled`() = runTest {
        // Given
        viewModel.updateName("Test Server")
        viewModel.updateHost("localhost")
        viewModel.updatePort("8080")
        viewModel.updateUsername("admin")
        viewModel.updatePassword("password")
        viewModel.updateUseHttps(false)

        // When
        viewModel.addServer()
        testDispatcher.scheduler.advanceUntilIdle()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            serverManager.addServer(
                match { serverConfig ->
                    serverConfig.name == "Test Server" &&
                    serverConfig.url == "http://localhost:8080" &&
                    serverConfig.username == "admin" &&
                    serverConfig.password == "password"
                }
            )
        }
        assertTrue(viewModel.serverAdded.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `addServer should create HTTPS server config when HTTPS enabled`() = runTest {
        // Given
        viewModel.updateName("Secure Server")
        viewModel.updateHost("example.com")
        viewModel.updatePort("443")
        viewModel.updateUsername("user")
        viewModel.updatePassword("secret")
        viewModel.updateUseHttps(true)

        // When
        viewModel.addServer()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            serverManager.addServer(
                match { serverConfig ->
                    serverConfig.name == "Secure Server" &&
                    serverConfig.url == "https://example.com:443" &&
                    serverConfig.username == "user" &&
                    serverConfig.password == "secret"
                }
            )
        }
        assertTrue(viewModel.serverAdded.value)
    }

    @Test
    fun `addServer should use default port when port is invalid`() = runTest {
        // Given
        viewModel.updateName("Test Server")
        viewModel.updateHost("localhost")
        viewModel.updatePort("invalid")
        viewModel.updateUseHttps(false)

        // When
        viewModel.addServer()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            serverManager.addServer(
                match { serverConfig ->
                    serverConfig.url == "http://localhost:8080"
                }
            )
        }
    }

    @Test
    fun `addServer should use default port when port is empty`() = runTest {
        // Given
        viewModel.updateName("Test Server")
        viewModel.updateHost("localhost")
        viewModel.updatePort("")
        viewModel.updateUseHttps(false)

        // When
        viewModel.addServer()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            serverManager.addServer(
                match { serverConfig ->
                    serverConfig.url == "http://localhost:8080"
                }
            )
        }
    }

    @Test
    fun `addServer should handle server manager exception`() = runTest {
        // Given
        val exceptionMessage = "Server already exists"
        coEvery { serverManager.addServer(any()) } throws Exception(exceptionMessage)

        viewModel.updateName("Test Server")
        viewModel.updateHost("localhost")

        // When
        viewModel.addServer()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(exceptionMessage, viewModel.error.value)
        assertFalse(viewModel.serverAdded.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `addServer should handle exception without message`() = runTest {
        // Given
        coEvery { serverManager.addServer(any()) } throws RuntimeException()

        viewModel.updateName("Test Server")
        viewModel.updateHost("localhost")

        // When
        viewModel.addServer()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals("Failed to add server", viewModel.error.value)
        assertFalse(viewModel.serverAdded.value)
    }

    @Test
    fun `addServer should set loading state correctly`() = runTest {
        // Given - make addServer suspend so we can test the loading state
        coEvery { serverManager.addServer(any()) } coAnswers {
            delay(100) // Add a suspension point
        }

        viewModel.updateName("Test Server")
        viewModel.updateHost("localhost")

        // When
        viewModel.addServer()

        // Advance to execute the coroutine up to the first suspension point (delay)
        testDispatcher.scheduler.runCurrent()

        // Then - loading should be true after starting
        assertTrue(viewModel.isLoading.value)

        // Complete the operation
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - loading should be false after completion
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun `clearError should reset error state`() {
        // Given - set an error
        coEvery { serverManager.addServer(any()) } throws Exception("Test error")
        viewModel.updateName("Test")
        viewModel.updateHost("localhost")

        runTest {
            viewModel.addServer()
        testDispatcher.scheduler.advanceUntilIdle()
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals("Test error", viewModel.error.value)

            // When
            viewModel.clearError()

            // Then
            assertNull(viewModel.error.value)
        }
    }

    @Test
    fun `addServer should create server config with null credentials when not provided`() = runTest {
        // Given
        viewModel.updateName("Public Server")
        viewModel.updateHost("public.example.com")
        viewModel.updatePort("8080")
        // Don't set username and password

        // When
        viewModel.addServer()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            serverManager.addServer(
                match { serverConfig ->
                    serverConfig.name == "Public Server" &&
                    serverConfig.url == "http://public.example.com:8080" &&
                    serverConfig.username == null &&
                    serverConfig.password == null
                }
            )
        }
    }

    @Test
    fun `addServer should construct URL correctly with host containing existing protocol`() = runTest {
        // Given
        viewModel.updateName("Test Server")
        viewModel.updateHost("localhost")
        viewModel.updatePort("9090")
        viewModel.updateUseHttps(true)

        // When
        viewModel.addServer()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            serverManager.addServer(
                match { serverConfig ->
                    serverConfig.url == "https://localhost:9090"
                }
            )
        }
    }
}