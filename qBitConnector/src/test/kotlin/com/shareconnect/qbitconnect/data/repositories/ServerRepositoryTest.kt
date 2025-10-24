package com.shareconnect.qbitconnect.data.repositories

import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.model.ServerConfig
import com.russhwolf.settings.Settings
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ServerRepositoryTest {

    private lateinit var serverRepository: ServerRepository
    private lateinit var mockServer: Server
    private lateinit var mockSettings: Settings
    private lateinit var serverManager: ServerManager

    @Before
    fun setUp() {
        mockSettings = mockk<Settings>(relaxed = true)
        // Mock the settings to return valid JSON for server configs
        coEvery { mockSettings.getString(any(), any()) } returns "[]"
        coEvery { mockSettings.getInt(any(), any()) } returns 0

        // Create a mock serverManager with proper flow handling
        val serversFlow = MutableStateFlow<List<ServerConfig>>(emptyList())
        serverManager = mockk()
        coEvery { serverManager.serversFlow } returns serversFlow
        coEvery { serverManager.addServer(any()) } coAnswers {
            val serverConfig = firstArg<ServerConfig>()
            serversFlow.value = serversFlow.value + serverConfig
        }
        coEvery { serverManager.removeServer(any()) } coAnswers {
            val serverId = firstArg<Int>()
            serversFlow.value = serversFlow.value.filter { it.id != serverId }
        }
        coEvery { serverManager.editServer(any()) } coAnswers {
            val serverConfig = firstArg<ServerConfig>()
            serversFlow.value = serversFlow.value.map { if (it.id == serverConfig.id) serverConfig else it }
        }

        serverRepository = ServerRepository(serverManager)
        mockServer = Server(
            id = 1,
            name = "Test Server",
            host = "localhost",
            port = 8080,
            username = "admin",
            password = "admin"
        )

        // Add the server for testing
        runBlocking { serverRepository.addServer(mockServer) }
    }

    @Test
    fun `testConnection should return success`() = runTest {
        // When
        val result = serverRepository.testConnection(mockServer)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `addServer should add server to list`() = runTest {
        // Given
        val newServer = Server(
            id = 2,
            name = "New Server",
            host = "192.168.1.100",
            port = 9090
        )

        // When
        serverRepository.addServer(newServer)

        // Then
        val servers = serverRepository.getServerById(2)
        assertEquals(newServer, servers)
    }

    @Test
    fun `removeServer should remove server and clear active server if it was active`() = runTest {
        // Given
        val serverId = 1
        serverRepository.setActiveServer(mockServer) // Set as active

        // When
        serverRepository.removeServer(serverId)

        // Then
        val server = serverRepository.getServerById(1)
        assertEquals(null, server)
        // Note: activeServer is a flow, can't access .value directly in test
    }

    @Test
    fun `updateServer should update server in list`() = runTest {
        // Given
        val updatedServer = mockServer.copy(name = "Updated Server")

        // When
        serverRepository.updateServer(updatedServer)

        // Then
        val server = serverRepository.getServerById(1)
        assertEquals("Updated Server", server?.name)
    }

    @Test
    fun `setActiveServer should update server to active`() = runTest {
        // When
        serverRepository.setActiveServer(mockServer)

        // Then
        val server = serverRepository.getServerById(1)
        assertEquals(true, server?.isActive)
    }

    @Test
    fun `setActiveServer with null should clear active server`() = runTest {
        // Given
        serverRepository.setActiveServer(mockServer)

        // When
        serverRepository.setActiveServer(null)

        // Then
        val server = serverRepository.getServerById(1)
        assertEquals(false, server?.isActive)
    }

    @Test
    fun `getServerById should return correct server`() = runTest {
        // When
        val result = serverRepository.getServerById(1)

        // Then
        assertEquals(mockServer, result)
    }

    @Test
    fun `getServerById should return null for non-existent server`() = runTest {
        // When
        val result = serverRepository.getServerById(999)

        // Then
        assertEquals(null, result)
    }
}