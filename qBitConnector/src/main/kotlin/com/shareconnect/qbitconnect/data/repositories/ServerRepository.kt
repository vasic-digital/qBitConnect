package com.shareconnect.qbitconnect.data.repositories

import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.model.ServerConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ServerRepository(
    private val serverManager: ServerManager
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _servers = MutableStateFlow<List<Server>>(emptyList())
    val servers: Flow<List<Server>> = _servers.asStateFlow()

    private val _activeServer = MutableStateFlow<Server?>(null)
    val activeServer: Flow<Server?> = _activeServer.asStateFlow()

    init {
        // Initialize from ServerManager
        _servers.value = serverManager.serversFlow.value.map { it.toServer() }

        // Listen to ServerManager changes
        serverManager.serversFlow
            .onEach { serverConfigs ->
                _servers.value = serverConfigs.map { it.toServer() }
            }
            .launchIn(scope)
    }

    suspend fun addServer(server: Server) {
        val serverConfig = server.toServerConfig()
        serverManager.addServer(serverConfig)
        // Update local state immediately
        _servers.value = _servers.value + server
    }

    suspend fun removeServer(serverId: Int) {
        serverManager.removeServer(serverId)
        // Update local state immediately
        _servers.value = _servers.value.filter { it.id != serverId }
        // If the removed server was active, clear active server
        if (_activeServer.value?.id == serverId) {
            _activeServer.value = null
        }
    }

    suspend fun updateServer(updatedServer: Server) {
        val serverConfig = updatedServer.toServerConfig()
        serverManager.editServer(serverConfig)
        // Update local state immediately
        _servers.value = _servers.value.map { if (it.id == updatedServer.id) updatedServer else it }
        // Update active server if it was modified
        if (_activeServer.value?.id == updatedServer.id) {
            _activeServer.value = updatedServer
        }
    }

    suspend fun setActiveServer(server: Server?) {
        // Clear active status from previous active server
        _activeServer.value?.let { previousActive ->
            updateServer(previousActive.copy(isActive = false))
        }

        _activeServer.value = server
        // Update the server's active status
        if (server != null) {
            updateServer(server.copy(isActive = true))
        }
    }

    fun getServerById(serverId: Int): Server? {
        return _servers.value.find { it.id == serverId }
    }

    suspend fun testConnection(server: Server): Result<Unit> {
        // TODO: Implement actual connection test
        return Result.success(Unit)
    }

    private fun ServerConfig.toServer(): Server {
        val url = this.url.removePrefix("http://").removePrefix("https://")
        val hostAndPort = url.split(":").let {
            if (it.size == 2) {
                Pair(it[0], it[1].toIntOrNull() ?: 8080)
            } else {
                Pair(url, if (protocol == com.shareconnect.qbitconnect.model.Protocol.HTTPS) 443 else 80)
            }
        }

        return Server(
            id = this.id,
            name = this.displayName,
            host = hostAndPort.first,
            port = hostAndPort.second,
            username = this.username,
            password = this.password,
            useHttps = this.protocol == com.shareconnect.qbitconnect.model.Protocol.HTTPS,
            isActive = false // Will be managed by activeServer flow
        )
    }

    private fun Server.toServerConfig(): ServerConfig {
        val url = if (useHttps) "https://$host:$port" else "http://$host:$port"
        return ServerConfig(
            id = this.id,
            name = this.name,
            url = url,
            username = this.username,
            password = this.password
        )
    }
}