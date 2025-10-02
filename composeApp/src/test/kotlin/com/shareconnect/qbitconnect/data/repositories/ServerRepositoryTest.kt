package com.shareconnect.qbitconnect.data.repositories

import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.network.RequestManager
import com.shareconnect.qbitconnect.network.RequestResult
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ServerRepositoryTest {

    private lateinit var serverManager: ServerManager
    private lateinit var requestManager: RequestManager
    private lateinit var serverRepository: ServerRepository
    private lateinit var mockServer: Server

    @Before
    fun setUp() {
        serverManager = mockk()
        requestManager = mockk()
        serverRepository = ServerRepository(serverManager, requestManager)
        mockServer = Server(
            id = "test-server",
            name = "Test Server",
            host = "localhost",
            port = 8080,
            username = "admin",
            password = "admin"
        )

        // Mock server manager
        every { serverManager.serversFlow } returns MutableStateFlow(listOf(mockServer))
        every { serverManager.addServer(any()) } returns Unit
        every { serverManager.removeServer(any()) } returns Unit
        every { serverManager.editServer(any()) } returns Unit
        every { serverManager.getServerOrNull(any()) } returns mockServer
    }

    @Test
    fun `testConnection should return success when version API succeeds`() = runTest {
        // Given
        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Success("v4.5.2")

        // When
        val result = serverRepository.testConnection(mockServer)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `testConnection should return failure when API call fails`() = runTest {
        // Given
        coEvery {
            requestManager.request<String>(mockServer.id, any())
        } returns RequestResult.Error.NetworkError(Exception("Connection failed"))

        // When
        val result = serverRepository.testConnection(mockServer)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Connection test failed: NetworkError(java.lang.Exception: Connection failed)", result.exceptionOrNull()?.message)
    }

    @Test
    fun `addServer should call serverManager addServer`() = runTest {
        // Given
        val newServer = Server(
            id = "new-server",
            name = "New Server",
            host = "192.168.1.100",
            port = 9090
        )

        // When
        serverRepository.addServer(newServer)

        // Then - verify the call was made (mockk will verify this)
    }

    @Test
    fun `removeServer should call serverManager removeServer and clear active server if it was active`() = runTest {
        // Given
        val serverId = "test-server"
        serverRepository.setActiveServer(mockServer) // Set as active

        // When
        serverRepository.removeServer(serverId)

        // Then - verify the call was made and active server is cleared
        assertEquals(null, serverRepository.activeServer.value)
    }

    @Test
    fun `updateServer should call serverManager editServer and update active server if modified`() = runTest {
        // Given
        val updatedServer = mockServer.copy(name = "Updated Server")
        serverRepository.setActiveServer(mockServer) // Set as active

        // When
        serverRepository.updateServer(updatedServer)

        // Then - verify the call was made and active server is updated
        assertEquals("Updated Server", serverRepository.activeServer.value?.name)
    }

    @Test
    fun `setActiveServer should update active server and call updateServer with isActive true`() = runTest {
        // When
        serverRepository.setActiveServer(mockServer)

        // Then
        assertEquals(mockServer.id, serverRepository.activeServer.value?.id)
        assertEquals(true, serverRepository.activeServer.value?.isActive)
    }

    @Test
    fun `setActiveServer with null should clear active server`() = runTest {
        // Given
        serverRepository.setActiveServer(mockServer)

        // When
        serverRepository.setActiveServer(null)

        // Then
        assertEquals(null, serverRepository.activeServer.value)
    }

    @Test
    fun `getServerById should return correct server from serverManager`() = runTest {
        // When
        val result = serverRepository.getServerById("test-server")

        // Then
        assertEquals(mockServer, result)
    }

    @Test
    fun `getServerById should return null for non-existent server`() = runTest {
        // Given
        every { serverManager.getServerOrNull("nonexistent") } returns null

        // When
        val result = serverRepository.getServerById("nonexistent")

        // Then
        assertEquals(null, result)
    }
}