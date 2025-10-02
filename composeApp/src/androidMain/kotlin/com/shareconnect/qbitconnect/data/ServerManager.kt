package com.shareconnect.qbitconnect.data

import android.content.Context
import android.content.SharedPreferences
import com.shareconnect.qbitconnect.data.models.ServerConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class ServerManager(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val prefs: SharedPreferences = context.getSharedPreferences("servers", Context.MODE_PRIVATE)

    private val _serversFlow = MutableStateFlow(loadServers())
    val serversFlow = _serversFlow.asStateFlow()

    private fun loadServers(): List<ServerConfig> {
        val serversJson = prefs.getString("server_configs", "[]") ?: "[]"
        return try {
            json.decodeFromString<List<ServerConfig>>(serversJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun saveServers(servers: List<ServerConfig>) {
        val serversJson = json.encodeToString(servers)
        prefs.edit().putString("server_configs", serversJson).apply()
        _serversFlow.value = servers
    }

    fun getServer(serverId: Int) =
        getServerOrNull(serverId) ?: throw IllegalStateException("Couldn't find server with id $serverId")

    fun getServerOrNull(serverId: Int) = serversFlow.value.find { it.id == serverId }

    fun addServer(serverConfig: ServerConfig) {
        val currentServers = serversFlow.value
        val lastId = prefs.getInt("last_server_id", 0)
        val newId = lastId + 1

        val newServer = serverConfig.copy(id = newId)
        val updatedServers = currentServers + newServer

        saveServers(updatedServers)
        prefs.edit().putInt("last_server_id", newId).apply()
    }

    fun editServer(serverConfig: ServerConfig) {
        val currentServers = serversFlow.value
        val updatedServers = currentServers.map {
            if (it.id == serverConfig.id) serverConfig else it
        }

        saveServers(updatedServers)
    }

    fun removeServer(serverId: Int) {
        val currentServers = serversFlow.value
        val updatedServers = currentServers.filter { it.id != serverId }

        saveServers(updatedServers)
    }

    fun reorderServer(from: Int, to: Int) {
        val currentServers = serversFlow.value.toMutableList()
        val server = currentServers.removeAt(from)
        currentServers.add(to, server)

        saveServers(currentServers)
    }
}