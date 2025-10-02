package com.shareconnect.qbitconnect.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.shareconnect.qbitconnect.data.repositories.ServerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel(
    private val serverRepository: ServerRepository
) : ViewModel() {

    private val _theme = MutableStateFlow("system")
    val theme: StateFlow<String> = _theme.asStateFlow()

    private val _enableDynamicColors = MutableStateFlow(true)
    val enableDynamicColors: StateFlow<Boolean> = _enableDynamicColors.asStateFlow()

    fun updateTheme(theme: String) {
        _theme.value = theme
    }

    fun updateDynamicColors(enabled: Boolean) {
        _enableDynamicColors.value = enabled
    }
}