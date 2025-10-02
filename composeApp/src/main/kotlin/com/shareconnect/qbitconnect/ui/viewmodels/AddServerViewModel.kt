package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.shareconnect.qbitconnect.data.repositories.ServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddServerViewModel(
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _host = MutableStateFlow("")
    val host: StateFlow<String> = _host.asStateFlow()

    private val _port = MutableStateFlow(8080)
    val port: StateFlow<Int> = _port.asStateFlow()

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

    fun updatePort(port: Int) {
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
        // TODO: Implement add server logic
        _serverAdded.value = true
    }

    fun clearError() {
        _error.value = null
    }
}