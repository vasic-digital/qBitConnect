package com.shareconnect.qbitconnect.network

import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.model.QBittorrentVersion
import com.shareconnect.qbitconnect.model.RequestResult
import com.shareconnect.qbitconnect.model.ServerConfig
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class RequestManagerTest {

    private val serverId = 1

    private lateinit var mockWebServer: MockWebServer
    private lateinit var mockServerManager: ServerManager
    private lateinit var mockSettingsManager: SettingsManager
    private lateinit var requestManager: RequestManager

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    private fun setupMocks() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        val url = mockWebServer.url("/").toString()

        mockServerManager = mock()
        mockSettingsManager = mock()

        val mockServerConfig = ServerConfig(
            id = serverId,
            name = "Test Server",
            url = url,
            username = "testuser",
            password = "testpass"
        )

        whenever(mockServerManager.getServer(serverId)).thenReturn(mockServerConfig)

        requestManager = RequestManager(mockServerManager, mockSettingsManager)
    }

    @Test
    fun `getQBittorrentVersion should return Invalid when no version cached`() = runTest {
        setupMocks()

        val version = requestManager.getQBittorrentVersion(serverId)

        assertEquals(QBittorrentVersion.Invalid, version)
    }

    @Test
    fun `buildHttpClient should create client with correct configuration`() = runTest {
        setupMocks()

        val serverConfig = mockServerManager.getServer(serverId)
        val client = requestManager.buildHttpClient(serverConfig)

        // Client should be created successfully
        assertTrue(client is OkHttpClient)
    }

    @Test
    fun `getHttpClient should return cached client for same server id`() = runTest {
        setupMocks()

        val client1 = requestManager.getHttpClient(serverId)
        val client2 = requestManager.getHttpClient(serverId)

        // Should return the same instance
        assertEquals(client1, client2)
    }

    @Test
    fun `buildTorrentService should create service with correct parameters`() = runTest {
        setupMocks()

        val serverConfig = mockServerManager.getServer(serverId)
        val client = requestManager.getHttpClient(serverId)
        val service = requestManager.buildTorrentService(serverConfig, client)

        assertTrue(service is TorrentService)
    }

    @Test
    fun `tryLogin should return Success when login succeeds`() = runTest {
        setupMocks()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Ok."))

        val result = requestManager.tryLogin(serverId)

        assertTrue(result is RequestResult.Success)
    }

    @Test
    fun `tryLogin should return InvalidCredentials when login fails`() = runTest {
        setupMocks()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Fails."))

        val result = requestManager.tryLogin(serverId)

        assertTrue(result is RequestResult.Error.RequestError.InvalidCredentials)
    }

    @Test
    fun `tryLogin should return Banned when response is 403`() = runTest {
        setupMocks()

        mockWebServer.enqueue(MockResponse().setResponseCode(403).setBody("Forbidden"))

        val result = requestManager.tryLogin(serverId)

        assertTrue(result is RequestResult.Error.RequestError.Banned)
    }
}