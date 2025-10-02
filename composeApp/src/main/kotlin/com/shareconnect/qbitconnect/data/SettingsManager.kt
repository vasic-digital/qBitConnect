package com.shareconnect.qbitconnect.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(
    private val context: Context? = null,
    private val testDataStore: DataStore<Preferences>? = null
) {

    private val themeKey = stringPreferencesKey("theme")
    private val enableDynamicColorsKey = booleanPreferencesKey("enable_dynamic_colors")

    private val dataStore: DataStore<Preferences> = testDataStore ?: context?.dataStore ?: throw IllegalArgumentException("Either context or testDataStore must be provided")

    val theme: Flow<Theme> = try {
        dataStore.data.map { preferences ->
            val themeString = preferences[themeKey] ?: Theme.SYSTEM_DEFAULT.name
            try {
                Theme.valueOf(themeString)
            } catch (e: IllegalArgumentException) {
                Theme.SYSTEM_DEFAULT
            }
        }
    } catch (e: Exception) {
        // Fallback to default theme if DataStore fails
        kotlinx.coroutines.flow.flowOf(Theme.SYSTEM_DEFAULT)
    }

    val enableDynamicColors: Flow<Boolean> = try {
        dataStore.data.map { preferences ->
            preferences[enableDynamicColorsKey] ?: true
        }
    } catch (e: Exception) {
        // Fallback to true if DataStore fails
        kotlinx.coroutines.flow.flowOf(true)
    }

    suspend fun setTheme(theme: Theme) {
        try {
            dataStore.edit { preferences ->
                preferences[themeKey] = theme.name
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setEnableDynamicColors(enabled: Boolean) {
        try {
            dataStore.edit { preferences ->
                preferences[enableDynamicColorsKey] = enabled
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }
}