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


package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.model.ServerConfig
import com.shareconnect.qbitconnect.model.RequestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddServerViewModel(
    private val serverManager: ServerManager
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _host = MutableStateFlow("")
    val host: StateFlow<String> = _host.asStateFlow()

    private val _port = MutableStateFlow("8080")
    val port: StateFlow<String> = _port.asStateFlow()

    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> = _username.asStateFlow()

    private val _password = MutableStateFlow<String?>(null)
    val password: StateFlow<String?> = _password.asStateFlow()

    private val _useHttps = MutableStateFlow(false)
    val useHttps: StateFlow<Boolean> = _useHttps.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _serverAdded = MutableStateFlow(false)
    val serverAdded: StateFlow<Boolean> = _serverAdded.asStateFlow()

    fun updateName(name: String) {
        _name.value = name
    }

    fun updateHost(host: String) {
        _host.value = host
    }

    fun updatePort(port: String) {
        _port.value = port
    }

    fun updateUsername(username: String?) {
        _username.value = username
    }

    fun updatePassword(password: String?) {
        _password.value = password
    }

    fun updateUseHttps(useHttps: Boolean) {
        _useHttps.value = useHttps
    }

    fun addServer() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val portInt = port.value.toIntOrNull() ?: 8080
                val url = if (useHttps.value) "https://${host.value}:$portInt" else "http://${host.value}:$portInt"
                val serverConfig = ServerConfig(
                    id = 0, // will be set by manager
                    name = name.value,
                    url = url,
                    username = username.value,
                    password = password.value,
                )
                serverManager.addServer(serverConfig)
                _serverAdded.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add server"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}