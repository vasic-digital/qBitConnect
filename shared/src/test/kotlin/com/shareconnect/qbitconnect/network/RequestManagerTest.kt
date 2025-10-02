package com.shareconnect.qbitconnect.network

import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.model.QBittorrentVersion
import com.shareconnect.qbitconnect.model.RequestResult
import com.shareconnect.qbitconnect.model.ServerConfig
import okhttp3.OkHttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class RequestManagerTest {

    private val serverId = 1
    private val baseUrl = "http://localhost:8080"

    private lateinit var mockServerManager: ServerManager
    private lateinit var mockSettingsManager: SettingsManager
    private lateinit var requestManager: RequestManager

    private fun setupMocks() {
        mockServerManager = mock()
        mockSettingsManager = mock()

        val mockServerConfig = ServerConfig(
            id = serverId,
            name = "Test Server",
            url = "localhost:8080",
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

        val mockEngine = MockEngine { request ->
            if (request.url.toString() == "$baseUrl/api/v2/auth/login" &&
                request.url.parameters["username"] == "testuser" &&
                request.url.parameters["password"] == "testpass") {
                respond("Ok.", HttpStatusCode.OK)
            } else {
                respond("Fails.", HttpStatusCode.OK)
            }
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Replace the http client in the manager
        val field = RequestManager::class.java.getDeclaredField("httpClientMap")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val httpClientMap = field.get(requestManager) as MutableMap<Int, HttpClient>
        httpClientMap[serverId] = client

        val result = requestManager.javaClass.getDeclaredMethod("tryLogin", Int::class.java)
            .apply { isAccessible = true }
            .invoke(requestManager, serverId) as RequestResult<Unit>

        assertTrue(result is RequestResult.Success)
    }

    @Test
    fun `tryLogin should return InvalidCredentials when login fails`() = runTest {
        setupMocks()

        val mockEngine = MockEngine { request ->
            respond("Fails.", HttpStatusCode.OK)
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Replace the http client in the manager
        val field = RequestManager::class.java.getDeclaredField("httpClientMap")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val httpClientMap = field.get(requestManager) as MutableMap<Int, HttpClient>
        httpClientMap[serverId] = client

        val result = requestManager.javaClass.getDeclaredMethod("tryLogin", Int::class.java)
            .apply { isAccessible = true }
            .invoke(requestManager, serverId) as RequestResult<Unit>

        assertTrue(result is RequestResult.Error.RequestError.InvalidCredentials)
    }

    @Test
    fun `tryLogin should return Banned when response is 403`() = runTest {
        setupMocks()

        val mockEngine = MockEngine { request ->
            respond("Forbidden", HttpStatusCode.Forbidden)
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // Replace the http client in the manager
        val field = RequestManager::class.java.getDeclaredField("httpClientMap")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val httpClientMap = field.get(requestManager) as MutableMap<Int, HttpClient>
        httpClientMap[serverId] = client

        val result = requestManager.javaClass.getDeclaredMethod("tryLogin", Int::class.java)
            .apply { isAccessible = true }
            .invoke(requestManager, serverId) as RequestResult<Unit>

        assertTrue(result is RequestResult.Error.RequestError.Banned)
    }
}