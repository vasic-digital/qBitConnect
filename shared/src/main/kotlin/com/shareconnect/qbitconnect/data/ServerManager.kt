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

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import com.shareconnect.qbitconnect.model.ServerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ServerManager(
    private val serverSettings: Settings,
) {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val _serversFlow = MutableStateFlow(
        json.decodeFromString<List<ServerConfig>>(serverSettings.getString(Keys.ServerConfigs, "[]")),
    )
    val serversFlow = _serversFlow.asStateFlow()

    fun getServer(serverId: Int) =
        getServerOrNull(serverId) ?: throw IllegalStateException("Couldn't find server with id $serverId")

    fun getServerOrNull(serverId: Int) = serversFlow.value.find { it.id == serverId }

    suspend fun addServer(serverConfig: ServerConfig) = withContext(Dispatchers.Default) {
        val serverConfigs = serversFlow.value
        val serverId = serverSettings[Keys.LastServerId, 0] + 1

        val newServerConfig = serverConfig.copy(id = serverId)
        val updatedServerConfigs = serverConfigs + newServerConfig

        serverSettings[Keys.ServerConfigs] = json.encodeToString(updatedServerConfigs)
        serverSettings[Keys.LastServerId] = serverId

        _serversFlow.value = updatedServerConfigs
        listeners.forEach { it.onServerAddedListener(newServerConfig) }
    }

    suspend fun editServer(serverConfig: ServerConfig) = withContext(Dispatchers.Default) {
        val serverConfigs = serversFlow.value
        val updatedServerConfigs = serverConfigs.map {
            if (it.id == serverConfig.id) serverConfig else it
        }

        serverSettings[Keys.ServerConfigs] = json.encodeToString(updatedServerConfigs)

        _serversFlow.value = updatedServerConfigs
        listeners.forEach { it.onServerChangedListener(serverConfig) }
    }

    suspend fun removeServer(serverId: Int) = withContext(Dispatchers.Default) {
        val serverConfigs = serversFlow.value
        val serverConfig = serverConfigs.find { it.id == serverId } ?: return@withContext
        val updatedServerConfigs = serverConfigs.filter { it.id != serverId }

        serverSettings[Keys.ServerConfigs] = json.encodeToString(updatedServerConfigs)

        _serversFlow.value = updatedServerConfigs
        listeners.forEach { it.onServerRemovedListener(serverConfig) }
    }

    fun reorderServer(from: Int, to: Int) {
        val serverConfigs = serversFlow.value
        val updatedServerConfigs = serverConfigs.toMutableList().apply {
            add(to, removeAt(from))
        }

        serverSettings[Keys.ServerConfigs] = json.encodeToString(updatedServerConfigs)
        _serversFlow.value = updatedServerConfigs
    }

    private val listeners = mutableListOf<ServerListener>()

    fun addServerListener(
        add: (ServerConfig) -> Unit = {},
        remove: (ServerConfig) -> Unit = {},
        change: (ServerConfig) -> Unit = {},
    ): ServerListener {
        val listener = object : ServerListener {
            override fun onServerAddedListener(serverConfig: ServerConfig) {
                add(serverConfig)
            }

            override fun onServerRemovedListener(serverConfig: ServerConfig) {
                remove(serverConfig)
            }

            override fun onServerChangedListener(serverConfig: ServerConfig) {
                change(serverConfig)
            }
        }
        listeners.add(listener)
        return listener
    }

    fun removeServerListener(serverListener: ServerListener) {
        listeners.remove(serverListener)
    }

    interface ServerListener {
        fun onServerAddedListener(serverConfig: ServerConfig)
        fun onServerRemovedListener(serverConfig: ServerConfig)
        fun onServerChangedListener(serverConfig: ServerConfig)
    }

    private object Keys {
        const val ServerConfigs = "serverConfigs"
        const val LastServerId = "lastServerId"
    }
}