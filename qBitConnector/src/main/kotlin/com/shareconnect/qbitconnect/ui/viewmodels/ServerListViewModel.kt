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
import com.shareconnect.qbitconnect.data.models.Server
import com.shareconnect.qbitconnect.data.repositories.ServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServerListViewModel(
    private val serverRepository: ServerRepository
) : ViewModel() {

    val servers = serverRepository.servers
    val activeServer = serverRepository.activeServer

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun addServer(server: Server) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Test connection first
                val testResult = serverRepository.testConnection(server)
                if (testResult.isSuccess) {
                    serverRepository.addServer(server)
                } else {
                    _error.value = "Failed to connect to server: ${testResult.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Failed to add server: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeServer(serverId: Int) {
        viewModelScope.launch {
            try {
                serverRepository.removeServer(serverId)
            } catch (e: Exception) {
                _error.value = "Failed to remove server: ${e.message}"
            }
        }
    }

    fun setActiveServer(server: Server) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Test connection before setting as active
                val testResult = serverRepository.testConnection(server)
                if (testResult.isSuccess) {
                    serverRepository.setActiveServer(server)
                } else {
                    _error.value = "Failed to connect to server: ${testResult.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Failed to set active server: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun testServerConnection(server: Server) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val result = serverRepository.testConnection(server)
                if (result.isFailure) {
                    _error.value = "Connection test failed: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                _error.value = "Connection test failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}