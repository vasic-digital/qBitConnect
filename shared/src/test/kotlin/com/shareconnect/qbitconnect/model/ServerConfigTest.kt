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

    @Test
    fun `AdvancedSettings should have default values`() {
        val settings = ServerConfig.AdvancedSettings()

        assertFalse(settings.trustSelfSignedCertificates)
        assertFalse(settings.basicAuth.isEnabled)
        assertEquals(null, settings.basicAuth.username)
        assertEquals(null, settings.basicAuth.password)
        assertEquals(null, settings.dnsOverHttps)
        assertTrue(settings.customHeaders.isEmpty())
    }

    @Test
    fun `AdvancedSettings should store custom values`() {
        val customHeaders = listOf(
            ServerConfig.AdvancedSettings.CustomHeader("X-Custom", "value1"),
            ServerConfig.AdvancedSettings.CustomHeader("X-Token", "value2")
        )

        val settings = ServerConfig.AdvancedSettings(
            trustSelfSignedCertificates = true,
            basicAuth = ServerConfig.AdvancedSettings.BasicAuth(true, "user", "pass"),
            dnsOverHttps = DnsOverHttps.Cloudflare,
            customHeaders = customHeaders
        )

        assertTrue(settings.trustSelfSignedCertificates)
        assertTrue(settings.basicAuth.isEnabled)
        assertEquals("user", settings.basicAuth.username)
        assertEquals("pass", settings.basicAuth.password)
        assertEquals(DnsOverHttps.Cloudflare, settings.dnsOverHttps)
        assertEquals(2, settings.customHeaders.size)
        assertEquals("X-Custom", settings.customHeaders[0].key)
        assertEquals("value1", settings.customHeaders[0].value)
    }

    @Test
    fun `ServerConfig should handle complex urls correctly`() {
        val config = ServerConfig(
            id = 1,
            name = "Complex Server",
            url = "https://example.com:8443/path",
            username = "user",
            password = "pass"
        )

        assertEquals("https://example.com:8443/path/", config.requestUrl)
        assertEquals("example.com:8443/path", config.visibleUrl)
        assertEquals(Protocol.HTTPS, config.protocol)
    }

    @Test
    fun `ServerConfig should handle edge cases`() {
        // Empty name
        val config1 = ServerConfig(1, "", "example.com", null, null)
        assertEquals("", config1.displayName)

        // URL with trailing slash
        val config2 = ServerConfig(2, null, "http://example.com/", null, null)
        assertEquals("http://example.com/", config2.requestUrl)

        // Case insensitive protocol detection
        val config3 = ServerConfig(3, null, "HTTPS://example.com", null, null)
        assertEquals(Protocol.HTTPS, config3.protocol)
    }
}

class DnsOverHttpsTest {

    @Test
    fun `Cloudflare should have correct configuration`() {
        val provider = DnsOverHttps.Cloudflare
        assertEquals("https://cloudflare-dns.com/dns-query", provider.url)
        assertTrue(provider.bootstrapDnsHosts.contains("1.1.1.1"))
        assertTrue(provider.bootstrapDnsHosts.contains("1.0.0.1"))
        assertTrue(provider.bootstrapDnsHosts.contains("162.159.36.1"))
    }

    @Test
    fun `Google should have correct configuration`() {
        val provider = DnsOverHttps.Google
        assertEquals("https://dns.google/dns-query", provider.url)
        assertTrue(provider.bootstrapDnsHosts.contains("8.8.8.8"))
        assertTrue(provider.bootstrapDnsHosts.contains("8.8.4.4"))
    }

    @Test
    fun `AdGuard should have correct configuration`() {
        val provider = DnsOverHttps.AdGuard
        assertEquals("https://dns-unfiltered.adguard.com/dns-query", provider.url)
        assertTrue(provider.bootstrapDnsHosts.contains("94.140.14.140"))
        assertTrue(provider.bootstrapDnsHosts.contains("94.140.14.141"))
    }

    @Test
    fun `all DNS providers should have valid configurations`() {
        DnsOverHttps.entries.forEach { provider ->
            // All providers should have HTTPS URLs
            assertTrue("${provider.name} should have HTTPS URL", provider.url.startsWith("https://"))

            // All providers should have at least one bootstrap DNS host
            assertTrue("${provider.name} should have bootstrap hosts", provider.bootstrapDnsHosts.isNotEmpty())

            // Bootstrap hosts should not be empty strings
            provider.bootstrapDnsHosts.forEach { host ->
                assertTrue("${provider.name} host should not be empty: $host", host.isNotBlank())
            }
        }
    }

    @Test
    fun `should have expected number of DNS providers`() {
        // Verify we have all expected providers
        assertTrue("Should have at least 10 DNS providers", DnsOverHttps.entries.size >= 10)

        // Verify specific providers exist
        val providerNames = DnsOverHttps.entries.map { it.name }.toSet()
        assertTrue(providerNames.contains("Cloudflare"))
        assertTrue(providerNames.contains("Google"))
        assertTrue(providerNames.contains("AdGuard"))
        assertTrue(providerNames.contains("Quad9"))
    }
}