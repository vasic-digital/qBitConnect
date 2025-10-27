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


package com.shareconnect.qbitconnect

import android.app.Application
import android.content.Context
import com.shareconnect.qbitconnect.di.DependencyContainer
import com.shareconnect.profilesync.ProfileSyncManager
import com.shareconnect.profilesync.models.ProfileData
import com.shareconnect.themesync.ThemeSyncManager
import com.shareconnect.historysync.HistorySyncManager
import com.shareconnect.rsssync.RSSSyncManager
import com.shareconnect.rsssync.models.RSSFeedData
import com.shareconnect.bookmarksync.BookmarkSyncManager
import com.shareconnect.preferencessync.PreferencesSyncManager
import com.shareconnect.languagesync.LanguageSyncManager
import com.shareconnect.languagesync.utils.LocaleHelper
import com.shareconnect.torrentsharingsync.TorrentSharingSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class App : Application() {
    lateinit var themeSyncManager: ThemeSyncManager
        private set

    lateinit var profileSyncManager: ProfileSyncManager
        private set

    lateinit var historySyncManager: HistorySyncManager
        private set

    lateinit var rssSyncManager: RSSSyncManager
        private set

    lateinit var bookmarkSyncManager: BookmarkSyncManager
        private set

    lateinit var preferencesSyncManager: PreferencesSyncManager
        private set

    lateinit var languageSyncManager: LanguageSyncManager
        private set

    lateinit var torrentSharingSyncManager: TorrentSharingSyncManager
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    override fun onCreate() {
        super.onCreate()
        // Disable Netty native transports to prevent epoll issues on Android
        System.setProperty("io.grpc.netty.shaded.io.netty.transport.noNative", "true")
        // Initialize manual dependency injection
        DependencyContainer.init(this)
        initializeLanguageSync()
        initializeTorrentSharingSync()
        initializeThemeSync()
        initializeProfileSync()
        initializeHistorySync()
        initializeRSSSync()
        initializeBookmarkSync()
        initializePreferencesSync()
        observeLanguageChanges()

        // Check if onboarding is needed
        checkAndLaunchOnboardingIfNeeded()
    }

    private fun observeLanguageChanges() {
        applicationScope.launch {
            languageSyncManager.languageChangeFlow.collect { languageData ->
                // Persist language change so it applies on next app start
                LocaleHelper.persistLanguage(this@App, languageData.languageCode)
            }
        }
    }

    private fun initializeThemeSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        themeSyncManager = ThemeSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(com.shareconnect.qbitconnect.R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        applicationScope.launch {
            delay(100) // Small delay to avoid port conflicts
            themeSyncManager.start()
        }
    }

    private fun initializeProfileSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        profileSyncManager = ProfileSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(com.shareconnect.qbitconnect.R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0.0",
            clientTypeFilter = ProfileData.TORRENT_CLIENT_QBITTORRENT  // Only sync qBittorrent profiles
        )

        applicationScope.launch {
            delay(200) // Small delay to avoid port conflicts
            profileSyncManager.start()
        }
    }

    private fun initializeHistorySync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        historySyncManager = HistorySyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(com.shareconnect.qbitconnect.R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        applicationScope.launch {
            delay(300) // Small delay to avoid port conflicts
            historySyncManager.start()
        }
    }

    private fun initializeRSSSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        rssSyncManager = RSSSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(com.shareconnect.qbitconnect.R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0.0",
            clientTypeFilter = RSSFeedData.TORRENT_CLIENT_QBITTORRENT  // Only sync qBittorrent RSS feeds
        )

        applicationScope.launch {
            delay(400) // Small delay to avoid port conflicts
            rssSyncManager.start()
        }
    }

    private fun initializeBookmarkSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        bookmarkSyncManager = BookmarkSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(com.shareconnect.qbitconnect.R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        applicationScope.launch {
            delay(500) // Small delay to avoid port conflicts
            bookmarkSyncManager.start()
        }
    }

    private fun initializePreferencesSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        preferencesSyncManager = PreferencesSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(com.shareconnect.qbitconnect.R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        applicationScope.launch {
            delay(600) // Small delay to avoid port conflicts
            preferencesSyncManager.start()
        }
    }

    private fun initializeLanguageSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        languageSyncManager = LanguageSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(com.shareconnect.qbitconnect.R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        applicationScope.launch {
            languageSyncManager.start()
        }
    }

    private fun initializeTorrentSharingSync() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        torrentSharingSyncManager = TorrentSharingSyncManager.getInstance(
            context = this,
            appId = packageName,
            appName = getString(com.shareconnect.qbitconnect.R.string.app_name),
            appVersion = packageInfo.versionName ?: "1.0.0"
        )

        applicationScope.launch {
            delay(700) // Small delay to avoid port conflicts
            torrentSharingSyncManager.start()
        }
    }

    private fun checkAndLaunchOnboardingIfNeeded() {
        // Check if onboarding has been completed
        val prefs = getSharedPreferences("onboarding_prefs", MODE_PRIVATE)
        val onboardingCompleted = prefs.getBoolean("onboarding_completed", false)

        if (!onboardingCompleted) {
            // Check if user has any existing data that would indicate they've used the app before
            applicationScope.launch {
                val hasExistingProfiles = runCatching {
                    profileSyncManager.getAllProfiles().first().isNotEmpty()
                }.getOrDefault(false)

                val hasExistingThemes = runCatching {
                    themeSyncManager.getAllThemes().first().isNotEmpty()
                }.getOrDefault(false)

                val hasExistingLanguage = runCatching {
                    languageSyncManager.getLanguagePreference() != null
                }.getOrDefault(false)

                // If no existing data, launch onboarding
                if (!hasExistingProfiles && !hasExistingThemes && !hasExistingLanguage) {
                    launchOnboarding()
                }
            }
        }
    }

    private fun launchOnboarding() {
        // Launch onboarding activity
        val intent = android.content.Intent(this, QBitConnectOnboardingActivity::class.java)
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}
