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