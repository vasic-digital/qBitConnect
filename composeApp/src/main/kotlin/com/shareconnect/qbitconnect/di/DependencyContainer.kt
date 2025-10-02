package com.shareconnect.qbitconnect.di

import android.content.Context
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.data.repositories.RSSRepository
import com.shareconnect.qbitconnect.data.repositories.SearchRepository
import com.shareconnect.qbitconnect.data.repositories.ServerRepository
import com.shareconnect.qbitconnect.data.repositories.TorrentRepository

object DependencyContainer {
    private lateinit var applicationContext: Context

    // Repositories (singletons)
    val serverRepository by lazy { ServerRepository() }
    val torrentRepository by lazy { TorrentRepository() }
    val rssRepository by lazy { RSSRepository() }
    val searchRepository by lazy { SearchRepository() }

    // SettingsManager (needs context)
    val settingsManager by lazy { SettingsManager(applicationContext) }

    fun init(context: Context) {
        if (!::applicationContext.isInitialized) {
            applicationContext = context.applicationContext
        }
    }
}