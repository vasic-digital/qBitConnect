package com.shareconnect.qbitconnect

import android.app.Application
import com.shareconnect.qbitconnect.di.DependencyContainer
import com.shareconnect.profilesync.ProfileSyncManager
import com.shareconnect.profilesync.models.ProfileData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class App : Application() {
    lateinit var profileSyncManager: ProfileSyncManager
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        // Initialize manual dependency injection
        DependencyContainer.init(this)
        initializeProfileSync()
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

        // Start profile sync in background
        applicationScope.launch {
            profileSyncManager.start()
        }
    }
}
