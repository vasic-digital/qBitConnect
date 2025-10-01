package com.shareconnect.qbitconnect.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ServerConfigTest {

    @Test
    fun `requestUrl should add http protocol when missing`() {
        val config = ServerConfig(
            id = 1,
            name = "Test Server",
            url = "localhost:8080",
            username = null,
            password = null
        )

        assertEquals("http://localhost:8080/", config.requestUrl)
    }

    @Test
    fun `requestUrl should preserve https protocol`() {
        val config = ServerConfig(
            id = 1,
            name = "Test Server",
            url = "https://localhost:8080",
            username = null,
            password = null
        )

        assertEquals("https://localhost:8080/", config.requestUrl)
    }

    @Test
    fun `requestUrl should add trailing slash when missing`() {
        val config = ServerConfig(
            id = 1,
            name = "Test Server",
            url = "http://localhost:8080",
            username = null,
            password = null
        )

        assertEquals("http://localhost:8080/", config.requestUrl)
    }

    @Test
    fun `requestUrl should not add extra slash when already present`() {
        val config = ServerConfig(
            id = 1,
            name = "Test Server",
            url = "http://localhost:8080/",
            username = null,
            password = null
        )

        assertEquals("http://localhost:8080/", config.requestUrl)
    }

    @Test
    fun `visibleUrl should remove protocol prefix`() {
        val config = ServerConfig(
            id = 1,
            name = "Test Server",
            url = "https://localhost:8080",
            username = null,
            password = null
        )

        assertEquals("localhost:8080", config.visibleUrl)
    }

    @Test
    fun `visibleUrl should handle http protocol`() {
        val config = ServerConfig(
            id = 1,
            name = "Test Server",
            url = "http://localhost:8080",
            username = null,
            password = null
        )

        assertEquals("localhost:8080", config.visibleUrl)
    }

    @Test
    fun `displayName should return name when present`() {
        val name = "My Server"
        val config = ServerConfig(
            id = 1,
            name = name,
            url = "localhost:8080",
            username = null,
            password = null
        )

        assertEquals(name, config.displayName)
    }

    @Test
    fun `displayName should return visibleUrl when name is null`() {
        val config = ServerConfig(
            id = 1,
            name = null,
            url = "localhost:8080",
            username = null,
            password = null
        )

        assertEquals("localhost:8080", config.displayName)
    }

    @Test
    fun `protocol should return HTTPS for https urls`() {
        val config = ServerConfig(
            id = 1,
            name = "Test Server",
            url = "https://localhost:8080",
            username = null,
            password = null
        )

        assertEquals(Protocol.HTTPS, config.protocol)
    }

    @Test
    fun `protocol should return HTTP for http urls`() {
        val config = ServerConfig(
            id = 1,
            name = "Test Server",
            url = "http://localhost:8080",
            username = null,
            password = null
        )

        assertEquals(Protocol.HTTP, config.protocol)
    }

    @Test
    fun `protocol should return HTTP for urls without protocol`() {
        val config = ServerConfig(
            id = 1,
            name = "Test Server",
            url = "localhost:8080",
            username = null,
            password = null
        )

        assertEquals(Protocol.HTTP, config.protocol)
    }

    @Test
    fun `AdvancedSettings BasicAuth should have correct defaults`() {
        val basicAuth = ServerConfig.AdvancedSettings.BasicAuth(
            isEnabled = true,
            username = "user",
            password = "pass"
        )

        assertTrue(basicAuth.isEnabled)
        assertEquals("user", basicAuth.username)
        assertEquals("pass", basicAuth.password)
    }

    @Test
    fun `AdvancedSettings CustomHeader should store key and value`() {
        val header = ServerConfig.AdvancedSettings.CustomHeader(
            key = "Authorization",
            value = "Bearer token"
        )

        assertEquals("Authorization", header.key)
        assertEquals("Bearer token", header.value)
    }

    @Test
    fun `Protocol enum should have HTTP and HTTPS values`() {
        assertEquals("HTTP", Protocol.HTTP.name)
        assertEquals("HTTPS", Protocol.HTTPS.name)
    }
}