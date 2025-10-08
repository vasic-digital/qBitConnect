package com.shareconnect.qbitconnect.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shareconnect.qbitconnect.App
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.data.Theme
import com.shareconnect.languagesync.models.LanguageData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsManager: SettingsManager,
    private val application: Application
) : ViewModel() {

    val theme = settingsManager.theme.flow
    val enableDynamicColors = settingsManager.enableDynamicColors.flow

    private val _currentLanguage = MutableStateFlow<LanguageData?>(null)
    val currentLanguage: StateFlow<LanguageData?> = _currentLanguage.asStateFlow()

    private val _availableLanguages = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val availableLanguages: StateFlow<List<Pair<String, String>>> = _availableLanguages.asStateFlow()

    init {
        loadLanguageData()
    }

    private fun loadLanguageData() {
        viewModelScope.launch {
            val app = application as App
            val languageSyncManager = app.languageSyncManager

            // Load current language
            _currentLanguage.value = languageSyncManager.getOrCreateDefault()

            // Load available languages
            _availableLanguages.value = LanguageData.getAvailableLanguages()

            // Observe language changes
            languageSyncManager.languageChangeFlow.collect { languageData ->
                _currentLanguage.value = languageData
            }
        }
    }

    fun setTheme(theme: Theme) {
        settingsManager.theme.value = theme
    }

    fun setEnableDynamicColors(enabled: Boolean) {
        settingsManager.enableDynamicColors.value = enabled
    }

    fun setLanguage(languageCode: String, displayName: String) {
        viewModelScope.launch {
            val app = application as App
            val languageSyncManager = app.languageSyncManager
            languageSyncManager.setLanguagePreference(languageCode, displayName)
        }
    }
}