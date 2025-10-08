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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize manual dependency injection
        DependencyContainer.init(this)
        initializeLanguageSync()
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
