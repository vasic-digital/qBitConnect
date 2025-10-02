package com.shareconnect.qbitconnect.data.repositories

import com.shareconnect.qbitconnect.data.models.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ServerRepository {

    private val _servers = MutableStateFlow<List<Server>>(emptyList())
    val servers: Flow<List<Server>> = _servers.asStateFlow()

    private val _activeServer = MutableStateFlow<Server?>(null)
    val activeServer: Flow<Server?> = _activeServer.asStateFlow()

    fun addServer(server: Server) {
        val currentServers = _servers.value
        _servers.value = currentServers + server
    }

    fun removeServer(serverId: String) {
        val currentServers = _servers.value
        _servers.value = currentServers.filter { it.id != serverId }

        // If the removed server was active, clear active server
        if (_activeServer.value?.id == serverId) {
            _activeServer.value = null
        }
    }

    fun updateServer(updatedServer: Server) {
        val currentServers = _servers.value
        _servers.value = currentServers.map { server ->
            if (server.id == updatedServer.id) updatedServer else server
        }

        // Update active server if it was modified
        if (_activeServer.value?.id == updatedServer.id) {
            _activeServer.value = updatedServer
        }
    }

    fun setActiveServer(server: Server?) {
        _activeServer.value = server
        // Update the server's active status
        if (server != null) {
            updateServer(server.copy(isActive = true))
        }
    }

    fun getServerById(serverId: String): Server? {
        return _servers.value.find { it.id == serverId }
    }

    suspend fun testConnection(server: Server): Result<Unit> {
        // TODO: Implement actual connection test
        return Result.success(Unit)
    }
}