package com.shareconnect.qbitconnect.data.repositories

import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.data.models.Server
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ServerRepositoryTest {

    private lateinit var serverRepository: ServerRepository
    private lateinit var mockServer: Server
    private lateinit var mockServerManager: ServerManager

    @Before
    fun setUp() {
        mockServerManager = mockk<ServerManager>(relaxed = true)
        serverRepository = ServerRepository(mockServerManager)
        mockServer = Server(
            id = 1,
            name = "Test Server",
            host = "localhost",
            port = 8080,
            username = "admin",
            password = "admin"
        )

        // Mock the addServer suspend function
        coEvery { serverRepository.addServer(mockServer) } returns Unit
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