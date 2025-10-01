package com.shareconnect.qbitconnect.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TorrentServiceTest {

    private val baseUrl = "http://localhost:8080"

    @Test
    fun `getVersion should return successful response when API call succeeds`() = runTest {
        val mockEngine = MockEngine { request ->
            if (request.url.toString() == "$baseUrl/api/v2/app/version") {
                respond("4.5.2", HttpStatusCode.OK)
            } else {
                respond("Not Found", HttpStatusCode.NotFound)
            }
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val service = TorrentService(client, baseUrl)
        val response = service.getVersion()

        assertEquals(200, response.code)
        assertEquals("4.5.2", response.body)
    }

    @Test
    fun `getVersion should return error response when API call fails`() = runTest {
        val mockEngine = MockEngine { request ->
            respond("Internal Server Error", HttpStatusCode.InternalServerError)
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val service = TorrentService(client, baseUrl)
        val response = service.getVersion()

        assertEquals(500, response.code)
        assertEquals("Internal Server Error", response.body)
    }

    @Test
    fun `getVersion should return error response when network fails`() = runTest {
        val mockEngine = MockEngine { request ->
            throw RuntimeException("Network error")
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val service = TorrentService(client, baseUrl)
        val response = service.getVersion()

        assertEquals(0, response.code)
        assertNull(response.body)
    }

    @Test
    fun `login should return successful response when login succeeds`() = runTest {
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

        val service = TorrentService(client, baseUrl)
        val response = service.login("testuser", "testpass")

        assertEquals(200, response.code)
        assertEquals("Ok.", response.body)
    }

    @Test
    fun `login should return failed response when credentials are invalid`() = runTest {
        val mockEngine = MockEngine { request ->
            respond("Fails.", HttpStatusCode.OK)
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val service = TorrentService(client, baseUrl)
        val response = service.login("wronguser", "wrongpass")

        assertEquals(200, response.code)
        assertEquals("Fails.", response.body)
    }

    @Test
    fun `login should return error response when network fails`() = runTest {
        val mockEngine = MockEngine { request ->
            throw RuntimeException("Network error")
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val service = TorrentService(client, baseUrl)
        val response = service.login("user", "pass")

        assertEquals(0, response.code)
        assertNull(response.body)
    }
}