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
    val serverRepository by lazy { ServerRepository(serverManager) }
    val torrentRepository by lazy { TorrentRepository(requestManager) }
    val rssRepository by lazy { RSSRepository(requestManager) }
    val searchRepository by lazy { SearchRepository() }

    fun init(context: Context) {
        if (!::applicationContext.isInitialized) {
            applicationContext = context.applicationContext
        }
    }
}