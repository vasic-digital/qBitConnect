package com.shareconnect.qbitconnect.di

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings
import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.data.repositories.RSSRepository
import com.shareconnect.qbitconnect.data.repositories.SearchRepository
import com.shareconnect.qbitconnect.data.repositories.ServerRepository
import com.shareconnect.qbitconnect.data.repositories.TorrentRepository
import com.shareconnect.qbitconnect.network.RequestManager

object DependencyContainer {
    private lateinit var applicationContext: Context

    // Data managers
    val serverManager by lazy { com.shareconnect.qbitconnect.data.ServerManager(SharedPreferencesSettings(applicationContext.getSharedPreferences("servers", Context.MODE_PRIVATE))) }
    val settingsManager by lazy { com.shareconnect.qbitconnect.data.SettingsManager(SharedPreferencesSettings(applicationContext.getSharedPreferences("settings", Context.MODE_PRIVATE))) }

    // Network
    val requestManager by lazy { RequestManager(serverManager, settingsManager) }

    // Repositories (singletons)
    val serverRepository by lazy { ServerRepository() }
    val torrentRepository by lazy { TorrentRepository(requestManager) }
    val rssRepository by lazy { RSSRepository(requestManager) }
    val searchRepository by lazy { SearchRepository() }

    fun init(context: Context) {
        if (!::applicationContext.isInitialized) {
            applicationContext = context.applicationContext
        }
    }
}