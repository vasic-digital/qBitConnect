package com.shareconnect.qbitconnect.di

import android.content.Context
import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.network.RequestManager


object DependencyContainer {
    private lateinit var applicationContext: Context

    // Data managers
    val serverManager by lazy { ServerManager(applicationContext) }
    val settingsManager by lazy { SettingsManager(applicationContext) }

    // Network
    val requestManager by lazy { RequestManager(serverManager, settingsManager) }

    // Repositories (singletons)
    // val serverRepository by lazy { ServerRepository() }
    // val torrentRepository by lazy { TorrentRepository() }
    // val rssRepository by lazy { RSSRepository() }
    // val searchRepository by lazy { SearchRepository() }

    fun init(context: Context) {
        if (!::applicationContext.isInitialized) {
            applicationContext = context.applicationContext
        }
    }
}