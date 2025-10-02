package com.shareconnect.qbitconnect.data

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import com.shareconnect.qbitconnect.model.ServerConfig
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ServerManagerTest {

    private val settings: Settings = MapSettings()
    private lateinit var serverManager: ServerManager

    private fun setupServerManager() {
        serverManager = ServerManager(settings)
    }

    @Test
    fun `getServer should return server when exists`() = runTest {
        setupServerManager()

        val serverConfig = ServerConfig(
            id = 1,
            name = "Test Server",
            url = "localhost:8080",
            username = null,
            password = null
        )

        serverManager.addServer(serverConfig)

        val retrieved = serverManager.getServer(1)
        assertNotNull(retrieved)
        assertEquals(serverConfig.copy(id = 1), retrieved)
    }

    @Test(expected = IllegalStateException::class)
    fun `getServer should throw exception when server not found`() = runTest {
        setupServerManager()

        serverManager.getServer(999)
    }

    @Test
    fun `getServerOrNull should return null when server not found`() = runTest {
        setupServerManager()

        val result = serverManager.getServerOrNull(999)
        assertNull(result)
    }

    @Test
    fun `addServer should add server with auto-generated id`() = runTest {
        setupServerManager()

        val serverConfig = ServerConfig(
            id = 0, // Will be replaced
            name = "Test Server",
            url = "localhost:8080",
            username = null,
            password = null
        )

        serverManager.addServer(serverConfig)

        val servers = serverManager.serversFlow.value
        assertEquals(1, servers.size)
        assertEquals("Test Server", servers[0].name)
        assertTrue(servers[0].id >= 0)
    }

    @Test
    fun `editServer should update existing server`() = runTest {
        setupServerManager()

        val originalConfig = ServerConfig(
            id = 0,
            name = "Original Server",
            url = "localhost:8080",
            username = null,
            password = null
        )

        serverManager.addServer(originalConfig)

        val addedServer = serverManager.serversFlow.value[0]
        val updatedConfig = addedServer.copy(name = "Updated Server")

        serverManager.editServer(updatedConfig)

        val servers = serverManager.serversFlow.value
        assertEquals(1, servers.size)
        assertEquals("Updated Server", servers[0].name)
    }

    @Test
    fun `removeServer should remove existing server`() = runTest {
        setupServerManager()

        val serverConfig = ServerConfig(
            id = 0,
            name = "Test Server",
            url = "localhost:8080",
            username = null,
            password = null
        )

        serverManager.addServer(serverConfig)

        val addedServer = serverManager.serversFlow.value[0]
        serverManager.removeServer(addedServer.id)

        val servers = serverManager.serversFlow.value
        assertEquals(0, servers.size)
    }

    @Test
    fun `removeServer should do nothing when server not found`() = runTest {
        setupServerManager()

        val serverConfig = ServerConfig(
            id = 0,
            name = "Test Server",
            url = "localhost:8080",
            username = null,
            password = null
        )

        serverManager.addServer(serverConfig)

        serverManager.removeServer(999) // Non-existent server

        val servers = serverManager.serversFlow.value
        assertEquals(1, servers.size) // Should still have the original server
    }

    @Test
    fun `reorderServer should reorder servers correctly`() = runTest {
        setupServerManager()

        val server1 = ServerConfig(
            id = 0,
            name = "Server 1",
            url = "localhost:8080",
            username = null,
            password = null
        )

        val server2 = ServerConfig(
            id = 0,
            name = "Server 2",
            url = "localhost:8081",
            username = null,
            password = null
        )

        val server3 = ServerConfig(
            id = 0,
            name = "Server 3",
            url = "localhost:8082",
            username = null,
            password = null
        )

        serverManager.addServer(server1)
        serverManager.addServer(server2)
        serverManager.addServer(server3)

        // Reorder: move first server to position 2
        serverManager.reorderServer(0, 2)

        val servers = serverManager.serversFlow.value
        assertEquals(3, servers.size)
        assertEquals("Server 2", servers[0].name)
        assertEquals("Server 3", servers[1].name)
        assertEquals("Server 1", servers[2].name)
    }

    @Test
    fun `addServerListener should notify on server addition`() = runTest {
        setupServerManager()

        var addedServer: ServerConfig? = null
        serverManager.addServerListener(
            add = { addedServer = it }
        )

        val serverConfig = ServerConfig(
            id = 0,
            name = "Test Server",
            url = "localhost:8080",
            username = null,
            password = null
        )

        serverManager.addServer(serverConfig)

        assertNotNull(addedServer)
        assertEquals("Test Server", addedServer?.name)
    }

    @Test
    fun `addServerListener should notify on server removal`() = runTest {
        setupServerManager()

        val serverConfig = ServerConfig(
            id = 0,
            name = "Test Server",
            url = "localhost:8080",
            username = null,
            password = null
        )

        serverManager.addServer(serverConfig)
        val addedServer = serverManager.serversFlow.value[0]

        var removedServer: ServerConfig? = null
        serverManager.addServerListener(
            remove = { removedServer = it }
        )

        serverManager.removeServer(addedServer.id)

        assertNotNull(removedServer)
        assertEquals("Test Server", removedServer?.name)
    }

    @Test
    fun `addServerListener should notify on server change`() = runTest {
        setupServerManager()

        val serverConfig = ServerConfig(
            id = 0,
            name = "Test Server",
            url = "localhost:8080",
            username = null,
            password = null
        )

        serverManager.addServer(serverConfig)
        val addedServer = serverManager.serversFlow.value[0]

        var changedServer: ServerConfig? = null
        serverManager.addServerListener(
            change = { changedServer = it }
        )

        val updatedConfig = addedServer.copy(name = "Updated Server")
        serverManager.editServer(updatedConfig)

        assertNotNull(changedServer)
        assertEquals("Updated Server", changedServer?.name)
    }

    @Test
    fun `removeServerListener should remove listener`() = runTest {
        setupServerManager()

        var callCount = 0
        val listener = serverManager.addServerListener(
            add = { callCount++ }
        )

        val serverConfig = ServerConfig(
            id = 0,
            name = "Test Server",
            url = "localhost:8080",
            username = null,
            password = null
        )

        serverManager.addServer(serverConfig)
        assertEquals(1, callCount)

        serverManager.removeServerListener(listener)
        serverManager.addServer(serverConfig.copy(name = "Another Server"))
        assertEquals(1, callCount) // Should not increment after removal
    }
}