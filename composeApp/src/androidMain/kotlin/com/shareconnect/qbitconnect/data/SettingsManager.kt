package com.shareconnect.qbitconnect.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    private val themeKey = stringPreferencesKey("theme")
    private val enableDynamicColorsKey = booleanPreferencesKey("enable_dynamic_colors")
    private val pureBlackDarkModeKey = booleanPreferencesKey("pure_black_dark_mode")
    private val showRelativeTimestampsKey = booleanPreferencesKey("show_relative_timestamps")
    private val sortKey = stringPreferencesKey("sort")
    private val isReverseSortingKey = booleanPreferencesKey("is_reverse_sorting")
    private val connectionTimeoutKey = intPreferencesKey("connection_timeout")
    private val autoRefreshIntervalKey = intPreferencesKey("auto_refresh_interval")
    private val notificationCheckIntervalKey = intPreferencesKey("notification_check_interval")
    private val areTorrentSwipeActionsEnabledKey = booleanPreferencesKey("are_torrent_swipe_actions_enabled")
    private val defaultTorrentStatusKey = stringPreferencesKey("default_torrent_state")
    private val areStatesCollapsedKey = booleanPreferencesKey("are_states_collapsed")
    private val areCategoriesCollapsedKey = booleanPreferencesKey("are_categories_collapsed")
    private val areTagsCollapsedKey = booleanPreferencesKey("are_tags_collapsed")
    private val areTrackersCollapsedKey = booleanPreferencesKey("are_trackers_collapsed")
    private val searchSortKey = stringPreferencesKey("search_sort")
    private val isReverseSearchSortingKey = booleanPreferencesKey("is_reverse_search_sort")
    private val checkUpdatesKey = booleanPreferencesKey("check_updates")

    val theme: Flow<Theme> = try {
        context.dataStore.data.map { preferences ->
            val themeString = preferences[themeKey] ?: Theme.SYSTEM_DEFAULT.name
            try {
                Theme.valueOf(themeString)
            } catch (e: IllegalArgumentException) {
                Theme.SYSTEM_DEFAULT
            }
        }
    } catch (e: Exception) {
        // Fallback to default theme if DataStore fails
        flowOf(Theme.SYSTEM_DEFAULT)
    }

    val enableDynamicColors: Flow<Boolean> = try {
        context.dataStore.data.map { preferences ->
            preferences[enableDynamicColorsKey] ?: true
        }
    } catch (e: Exception) {
        // Fallback to true if DataStore fails
        flowOf(true)
    }

    val pureBlackDarkMode: Flow<Boolean> = try {
        context.dataStore.data.map { preferences ->
            preferences[pureBlackDarkModeKey] ?: false
        }
    } catch (e: Exception) {
        flowOf(false)
    }

    val showRelativeTimestamps: Flow<Boolean> = try {
        context.dataStore.data.map { preferences ->
            preferences[showRelativeTimestampsKey] ?: true
        }
    } catch (e: Exception) {
        flowOf(true)
    }

    val sort: Flow<TorrentSort> = try {
        context.dataStore.data.map { preferences ->
            val sortString = preferences[sortKey] ?: TorrentSort.NAME.name
            try {
                TorrentSort.valueOf(sortString)
            } catch (e: IllegalArgumentException) {
                TorrentSort.NAME
            }
        }
    } catch (e: Exception) {
        flowOf(TorrentSort.NAME)
    }

    val isReverseSorting: Flow<Boolean> = try {
        context.dataStore.data.map { preferences ->
            preferences[isReverseSortingKey] ?: false
        }
    } catch (e: Exception) {
        flowOf(false)
    }

    val connectionTimeout: Flow<Int> = try {
        context.dataStore.data.map { preferences ->
            preferences[connectionTimeoutKey] ?: 10
        }
    } catch (e: Exception) {
        flowOf(10)
    }

    val autoRefreshInterval: Flow<Int> = try {
        context.dataStore.data.map { preferences ->
            preferences[autoRefreshIntervalKey] ?: 3
        }
    } catch (e: Exception) {
        flowOf(3)
    }

    val notificationCheckInterval: Flow<Int> = try {
        context.dataStore.data.map { preferences ->
            preferences[notificationCheckIntervalKey] ?: 15
        }
    } catch (e: Exception) {
        flowOf(15)
    }

    val areTorrentSwipeActionsEnabled: Flow<Boolean> = try {
        context.dataStore.data.map { preferences ->
            preferences[areTorrentSwipeActionsEnabledKey] ?: true
        }
    } catch (e: Exception) {
        flowOf(true)
    }

    val defaultTorrentStatus: Flow<TorrentFilter> = try {
        context.dataStore.data.map { preferences ->
            val statusString = preferences[defaultTorrentStatusKey] ?: TorrentFilter.ALL.name
            try {
                TorrentFilter.valueOf(statusString)
            } catch (e: IllegalArgumentException) {
                TorrentFilter.ALL
            }
        }
    } catch (e: Exception) {
        flowOf(TorrentFilter.ALL)
    }

    val areStatesCollapsed: Flow<Boolean> = try {
        context.dataStore.data.map { preferences ->
            preferences[areStatesCollapsedKey] ?: false
        }
    } catch (e: Exception) {
        flowOf(false)
    }

    val areCategoriesCollapsed: Flow<Boolean> = try {
        context.dataStore.data.map { preferences ->
            preferences[areCategoriesCollapsedKey] ?: false
        }
    } catch (e: Exception) {
        flowOf(false)
    }

    val areTagsCollapsed: Flow<Boolean> = try {
        context.dataStore.data.map { preferences ->
            preferences[areTagsCollapsedKey] ?: false
        }
    } catch (e: Exception) {
        flowOf(false)
    }

    val areTrackersCollapsed: Flow<Boolean> = try {
        context.dataStore.data.map { preferences ->
            preferences[areTrackersCollapsedKey] ?: false
        }
    } catch (e: Exception) {
        flowOf(false)
    }

    val searchSort: Flow<SearchSort> = try {
        context.dataStore.data.map { preferences ->
            val sortString = preferences[searchSortKey] ?: SearchSort.NAME.name
            try {
                SearchSort.valueOf(sortString)
            } catch (e: IllegalArgumentException) {
                SearchSort.NAME
            }
        }
    } catch (e: Exception) {
        flowOf(SearchSort.NAME)
    }

    val isReverseSearchSorting: Flow<Boolean> = try {
        context.dataStore.data.map { preferences ->
            preferences[isReverseSearchSortingKey] ?: false
        }
    } catch (e: Exception) {
        flowOf(false)
    }

    val checkUpdates: Flow<Boolean> = try {
        context.dataStore.data.map { preferences ->
            preferences[checkUpdatesKey] ?: true
        }
    } catch (e: Exception) {
        flowOf(true)
    }

    suspend fun setTheme(theme: Theme) {
        try {
            context.dataStore.edit { preferences ->
                preferences[themeKey] = theme.name
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setEnableDynamicColors(enabled: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[enableDynamicColorsKey] = enabled
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setPureBlackDarkMode(enabled: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[pureBlackDarkModeKey] = enabled
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setShowRelativeTimestamps(enabled: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[showRelativeTimestampsKey] = enabled
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setSort(sort: TorrentSort) {
        try {
            context.dataStore.edit { preferences ->
                preferences[sortKey] = sort.name
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setIsReverseSorting(reverse: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[isReverseSortingKey] = reverse
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setConnectionTimeout(timeout: Int) {
        try {
            context.dataStore.edit { preferences ->
                preferences[connectionTimeoutKey] = timeout
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setAutoRefreshInterval(interval: Int) {
        try {
            context.dataStore.edit { preferences ->
                preferences[autoRefreshIntervalKey] = interval
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setNotificationCheckInterval(interval: Int) {
        try {
            context.dataStore.edit { preferences ->
                preferences[notificationCheckIntervalKey] = interval
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setAreTorrentSwipeActionsEnabled(enabled: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[areTorrentSwipeActionsEnabledKey] = enabled
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setDefaultTorrentStatus(status: TorrentFilter) {
        try {
            context.dataStore.edit { preferences ->
                preferences[defaultTorrentStatusKey] = status.name
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setAreStatesCollapsed(collapsed: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[areStatesCollapsedKey] = collapsed
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setAreCategoriesCollapsed(collapsed: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[areCategoriesCollapsedKey] = collapsed
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setAreTagsCollapsed(collapsed: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[areTagsCollapsedKey] = collapsed
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setAreTrackersCollapsed(collapsed: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[areTrackersCollapsedKey] = collapsed
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setSearchSort(sort: SearchSort) {
        try {
            context.dataStore.edit { preferences ->
                preferences[searchSortKey] = sort.name
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setIsReverseSearchSorting(reverse: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[isReverseSearchSortingKey] = reverse
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }

    suspend fun setCheckUpdates(enabled: Boolean) {
        try {
            context.dataStore.edit { preferences ->
                preferences[checkUpdatesKey] = enabled
            }
        } catch (e: Exception) {
            // Silently fail if DataStore is not available
        }
    }
}

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM_DEFAULT,
}

enum class TorrentSort {
    NAME,
    STATUS,
    HASH,
    DOWNLOAD_SPEED,
    UPLOAD_SPEED,
    PRIORITY,
    ETA,
    SIZE,
    RATIO,
    PROGRESS,
    CONNECTED_SEEDS,
    TOTAL_SEEDS,
    CONNECTED_LEECHES,
    TOTAL_LEECHES,
    ADDITION_DATE,
    COMPLETION_DATE,
    LAST_ACTIVITY,
}

enum class SearchSort {
    NAME,
    SIZE,
    SEEDERS,
    LEECHERS,
    SEARCH_ENGINE,
}

enum class TorrentFilter {
    ALL,
    DOWNLOADING,
    SEEDING,
    COMPLETED,
    PAUSED,
    ACTIVE,
    INACTIVE,
    STALLED,
    CHECKING,
    ERRORED,
}