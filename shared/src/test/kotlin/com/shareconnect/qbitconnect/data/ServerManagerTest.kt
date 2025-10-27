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


package com.shareconnect.qbitconnect.data

import com.russhwolf.settings.MockSettings
import com.shareconnect.qbitconnect.model.ServerConfig
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ServerManagerTest {

    private lateinit var settings: MockSettings
    private lateinit var serverManager: ServerManager

    @Before
    fun setup() {
        settings = MockSettings()
        serverManager = ServerManager(settings)
    }

    @Test
    fun `initial state should have empty server list`() {
        assertTrue(serverManager.serversFlow.value.isEmpty())
    }

    @Test
    fun `addServer should add server with incremented ID`() = runTest {
        val serverConfig = ServerConfig(
            id = 0, // ID should be ignored and auto-assigned
            name = "Test Server",
            url = "http://localhost:8080",
            username = "user",
            password = "pass"
        )

        serverManager.addServer(serverConfig)

        val servers = serverManager.serversFlow.value
        assertEquals(1, servers.size)
        assertEquals(1, servers[0].id) // Should be auto-assigned as 1
        assertEquals("Test Server", servers[0].name)
        assertEquals("http://localhost:8080", servers[0].url)
    }

    @Test
    fun `addServer should increment ID for multiple servers`() = runTest {
        val server1 = ServerConfig(0, "Server 1", "http://server1.com", null, null)
        val server2 = ServerConfig(0, "Server 2", "http://server2.com", null, null)

        serverManager.addServer(server1)
        serverManager.addServer(server2)

        val servers = serverManager.serversFlow.value
        assertEquals(2, servers.size)
        assertEquals(1, servers[0].id)
        assertEquals(2, servers[1].id)
    }

    @Test
    fun `getServer should return correct server`() = runTest {
        val serverConfig = ServerConfig(0, "Test Server", "http://localhost", null, null)
        serverManager.addServer(serverConfig)

        val retrieved = serverManager.getServer(1)
        assertEquals("Test Server", retrieved.name)
        assertEquals("http://localhost", retrieved.url)
    }

    @Test
    fun `getServer should throw exception for non-existent server`() {
        assertFailsWith<IllegalStateException> {
            serverManager.getServer(999)
        }
    }

    @Test
    fun `getServerOrNull should return null for non-existent server`() {
        val server = serverManager.getServerOrNull(999)
        assertNull(server)
    }

    @Test
    fun `getServerOrNull should return server when exists`() = runTest {
        val serverConfig = ServerConfig(0, "Test Server", "http://localhost", null, null)
        serverManager.addServer(serverConfig)

        val retrieved = serverManager.getServerOrNull(1)
        assertNotNull(retrieved)
        assertEquals("Test Server", retrieved.name)
    }

    @Test
    fun `editServer should update existing server`() = runTest {
        val originalServer = ServerConfig(0, "Original", "http://original.com", null, null)
        serverManager.addServer(originalServer)

        val updatedServer = ServerConfig(1, "Updated", "http://updated.com", "user", "pass")
        serverManager.editServer(updatedServer)

        val servers = serverManager.serversFlow.value
        assertEquals(1, servers.size)
        assertEquals("Updated", servers[0].name)
        assertEquals("http://updated.com", servers[0].url)
        assertEquals("user", servers[0].username)
    }

    @Test
    fun `editServer should not affect other servers`() = runTest {
        val server1 = ServerConfig(0, "Server 1", "http://server1.com", null, null)
        val server2 = ServerConfig(0, "Server 2", "http://server2.com", null, null)

        serverManager.addServer(server1)
        serverManager.addServer(server2)

        val updatedServer2 = ServerConfig(2, "Updated Server 2", "http://updated.com", null, null)
        serverManager.editServer(updatedServer2)

        val servers = serverManager.serversFlow.value
        assertEquals(2, servers.size)
        assertEquals("Server 1", servers[0].name) // Should remain unchanged
        assertEquals("Updated Server 2", servers[1].name) // Should be updated
    }

    @Test
    fun `removeServer should remove correct server`() = runTest {
        val server1 = ServerConfig(0, "Server 1", "http://server1.com", null, null)
        val server2 = ServerConfig(0, "Server 2", "http://server2.com", null, null)

        serverManager.addServer(server1)
        serverManager.addServer(server2)

        serverManager.removeServer(1)

        val servers = serverManager.serversFlow.value
        assertEquals(1, servers.size)
        assertEquals("Server 2", servers[0].name)
        assertEquals(2, servers[0].id)
    }

    @Test
    fun `removeServer should do nothing for non-existent server`() = runTest {
        val server = ServerConfig(0, "Server", "http://server.com", null, null)
        serverManager.addServer(server)

        serverManager.removeServer(999) // Non-existent ID

        val servers = serverManager.serversFlow.value
        assertEquals(1, servers.size) // Should remain unchanged
    }

    @Test
    fun `reorderServer should move server correctly`() = runTest {
        val server1 = ServerConfig(0, "Server 1", "http://server1.com", null, null)
        val server2 = ServerConfig(0, "Server 2", "http://server2.com", null, null)
        val server3 = ServerConfig(0, "Server 3", "http://server3.com", null, null)

        serverManager.addServer(server1)
        serverManager.addServer(server2)
        serverManager.addServer(server3)

        // Move server from index 0 to index 2
        serverManager.reorderServer(0, 2)

        val servers = serverManager.serversFlow.value
        assertEquals(3, servers.size)
        assertEquals("Server 2", servers[0].name) // Server 2 moved to first
        assertEquals("Server 3", servers[1].name) // Server 3 moved to second
        assertEquals("Server 1", servers[2].name) // Server 1 moved to third
    }

    @Test
    fun `server listeners should be notified on add`() = runTest {
        var addedServer: ServerConfig? = null

        val listener = serverManager.addServerListener(
            add = { addedServer = it }
        )

        val serverConfig = ServerConfig(0, "Test Server", "http://localhost", null, null)
        serverManager.addServer(serverConfig)

        assertNotNull(addedServer)
        assertEquals("Test Server", addedServer.name)
        assertEquals(1, addedServer.id) // Should have auto-assigned ID
    }

    @Test
    fun `server listeners should be notified on remove`() = runTest {
        var removedServer: ServerConfig? = null

        val listener = serverManager.addServerListener(
            remove = { removedServer = it }
        )

        val serverConfig = ServerConfig(0, "Test Server", "http://localhost", null, null)
        serverManager.addServer(serverConfig)
        serverManager.removeServer(1)

        assertNotNull(removedServer)
        assertEquals("Test Server", removedServer.name)
        assertEquals(1, removedServer.id)
    }

    @Test
    fun `server listeners should be notified on change`() = runTest {
        var changedServer: ServerConfig? = null

        val listener = serverManager.addServerListener(
            change = { changedServer = it }
        )

        val serverConfig = ServerConfig(0, "Original", "http://localhost", null, null)
        serverManager.addServer(serverConfig)

        val updatedConfig = ServerConfig(1, "Updated", "http://updated.com", null, null)
        serverManager.editServer(updatedConfig)

        assertNotNull(changedServer)
        assertEquals("Updated", changedServer.name)
        assertEquals("http://updated.com", changedServer.url)
    }

    @Test
    fun `multiple listeners should all be notified`() = runTest {
        var listener1Called = false
        var listener2Called = false

        val listener1 = serverManager.addServerListener(add = { listener1Called = true })
        val listener2 = serverManager.addServerListener(add = { listener2Called = true })

        val serverConfig = ServerConfig(0, "Test", "http://localhost", null, null)
        serverManager.addServer(serverConfig)

        assertTrue(listener1Called)
        assertTrue(listener2Called)
    }

    @Test
    fun `removed listeners should not be notified`() = runTest {
        var listenerCalled = false

        val listener = serverManager.addServerListener(add = { listenerCalled = true })
        serverManager.removeServerListener(listener)

        val serverConfig = ServerConfig(0, "Test", "http://localhost", null, null)
        serverManager.addServer(serverConfig)

        assertTrue(!listenerCalled)
    }

    @Test
    fun `server data should persist across manager instances`() = runTest {
        // Add server with first manager instance
        val server = ServerConfig(0, "Persistent Server", "http://persistent.com", null, null)
        serverManager.addServer(server)

        // Create new manager instance with same settings
        val newManager = ServerManager(settings)

        val servers = newManager.serversFlow.value
        assertEquals(1, servers.size)
        assertEquals("Persistent Server", servers[0].name)
        assertEquals("http://persistent.com", servers[0].url)
        assertEquals(1, servers[0].id)
    }

    @Test
    fun `serversFlow should emit updates when servers change`() = runTest {
        val initialServers = serverManager.serversFlow.value
        assertTrue(initialServers.isEmpty())

        val server = ServerConfig(0, "Test", "http://localhost", null, null)
        serverManager.addServer(server)

        val updatedServers = serverManager.serversFlow.value
        assertEquals(1, updatedServers.size)
        assertEquals("Test", updatedServers[0].name)
    }
}