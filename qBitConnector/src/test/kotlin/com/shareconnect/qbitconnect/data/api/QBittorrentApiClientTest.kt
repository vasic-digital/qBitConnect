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


package com.shareconnect.qbitconnect.data.api

import com.shareconnect.qbitconnect.data.model.QBittorrentAddOptions
import com.shareconnect.qbitconnect.data.model.QBittorrentTorrent
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for QBittorrentApiClient
 *
 * Tests cover:
 * - Authentication (login/logout)
 * - Torrent operations (add, pause, resume, remove)
 * - Torrent information retrieval
 * - Transfer control (speed limits)
 * - Category management
 * - Error handling
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class QBittorrentApiClientTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: QBittorrentApiClient
    private val testUsername = "admin"
    private val testPassword = "adminpass"

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val baseUrl = mockWebServer.url("/").toString().removeSuffix("/")
        apiClient = QBittorrentApiClient(baseUrl, testUsername, testPassword)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test API client initialization`() {
        assertNotNull(apiClient)
    }

    @Test
    fun `test successful login`() = runBlocking {
        // Mock successful login response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("Ok."))

        val result = apiClient.login()

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest()
        assertEquals("/api/v2/auth/login", request.path)
    }

    @Test
    fun `test failed login with invalid credentials`() = runBlocking {
        // Mock failed login response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(401)
            .setBody("Fails."))

        val result = apiClient.login()

        assertTrue(result.isFailure)
    }

    @Test
    fun `test get torrents list`() = runBlocking {
        // Mock login
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Ok."))

        // Mock torrents list response
        val torrentsJson = """
            [{
                "hash": "test-hash-123",
                "name": "Test Torrent",
                "size": 1073741824,
                "progress": 0.5,
                "dlspeed": 102400,
                "upspeed": 51200,
                "eta": 3600,
                "state": "downloading",
                "num_seeds": 10,
                "num_leechs": 5,
                "ratio": 1.5,
                "category": "Movies",
                "tags": "test,sample",
                "added_on": 1234567890,
                "completion_on": 0,
                "tracker": "http://tracker.example.com",
                "save_path": "/downloads/torrents"
            }]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody(torrentsJson))

        val result = apiClient.getTorrents()

        assertTrue(result.isSuccess)
        val torrents = result.getOrNull()!!
        assertEquals(1, torrents.size)
        assertEquals("test-hash-123", torrents[0].hash)
        assertEquals("Test Torrent", torrents[0].name)
    }

    @Test
    fun `test add torrent by URL`() = runBlocking {
        // Mock login
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Ok."))

        // Mock add torrent response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("Ok."))

        val options = QBittorrentAddOptions(
            urls = "magnet:?xt=urn:btih:test",
            savepath = "/downloads",
            category = "Movies"
        )

        val result = apiClient.addTorrent(
            urls = options.urls!!,
            options = options
        )

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest() // Skip login
        val addRequest = mockWebServer.takeRequest()
        assertTrue(addRequest.path!!.startsWith("/api/v2/torrents/add"))
    }

    @Test
    fun `test pause torrents`() = runBlocking {
        // Mock login
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Ok."))

        // Mock pause response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("Ok."))

        val result = apiClient.pauseTorrents(listOf("hash1", "hash2"))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test resume torrents`() = runBlocking {
        // Mock login
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Ok."))

        // Mock resume response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("Ok."))

        val result = apiClient.resumeTorrents(listOf("hash1", "hash2"))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test delete torrents without data`() = runBlocking {
        // Mock login
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Ok."))

        // Mock delete response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("Ok."))

        val result = apiClient.deleteTorrents(listOf("hash1"), deleteFiles = false)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test delete torrents with data`() = runBlocking {
        // Mock login
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Ok."))

        // Mock delete response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("Ok."))

        val result = apiClient.deleteTorrents(listOf("hash1"), deleteFiles = true)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test set download speed limit`() = runBlocking {
        // Mock login
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Ok."))

        // Mock set limit response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("Ok."))

        val result = apiClient.setDownloadLimit(1048576) // 1 MB/s

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test set upload speed limit`() = runBlocking {
        // Mock login
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Ok."))

        // Mock set limit response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("Ok."))

        val result = apiClient.setUploadLimit(524288) // 512 KB/s

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test create category`() = runBlocking {
        // Mock login
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Ok."))

        // Mock create category response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("Ok."))

        val result = apiClient.createCategory("Movies", "/downloads/movies")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test remove categories`() = runBlocking {
        // Mock login
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Ok."))

        // Mock remove categories response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("Ok."))

        val result = apiClient.removeCategories(listOf("Movies", "TV Shows"))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test network error handling`() = runBlocking {
        // Shutdown server to simulate network error
        mockWebServer.shutdown()

        val result = apiClient.login()

        assertTrue(result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `test HTTP error code handling`() = runBlocking {
        // Mock 500 Internal Server Error
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(500)
            .setBody("Internal Server Error"))

        val result = apiClient.login()

        assertTrue(result.isFailure)
    }

    @Test
    fun `test authentication persistence`() = runBlocking {
        // Mock successful login with cookie
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("Ok.")
            .addHeader("Set-Cookie", "SID=test-session-id; Path=/; HttpOnly"))

        val loginResult = apiClient.login()
        assertTrue(loginResult.isSuccess)

        // Mock torrents call (should use existing authentication)
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("[]"))

        val torrentsResult = apiClient.getTorrents()
        assertTrue(torrentsResult.isSuccess)
    }

    @Test
    fun `test logout`() = runBlocking {
        // Mock login
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Ok."))
        apiClient.login()

        // Mock logout response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("Ok."))

        val result = apiClient.logout()

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test filter torrents by category`() = runBlocking {
        // Mock login
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Ok."))

        // Mock filtered torrents response
        mockWebServer.enqueue(MockResponse()
            .setResponseCode(200)
            .setBody("[]"))

        val result = apiClient.getTorrents(filter = "completed", category = "Movies")

        assertTrue(result.isSuccess)
        val request = mockWebServer.takeRequest() // Skip login
        val torrentsRequest = mockWebServer.takeRequest()
        assertTrue(torrentsRequest.path!!.contains("filter=completed"))
        assertTrue(torrentsRequest.path!!.contains("category=Movies"))
    }
}
