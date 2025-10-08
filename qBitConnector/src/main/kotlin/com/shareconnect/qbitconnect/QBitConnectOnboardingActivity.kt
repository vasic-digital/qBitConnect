package com.shareconnect.qbitconnect

import android.content.Intent
import com.shareconnect.onboarding.ui.OnboardingActivity
import com.shareconnect.onboarding.viewmodel.OnboardingViewModel

class QBitConnectOnboardingActivity : OnboardingActivity() {

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize viewModel with sync managers from App
        val app = application as App
        viewModel.initializeSyncManagers(
            themeManager = app.themeSyncManager,
            profileManager = app.profileSyncManager,
            languageManager = app.languageSyncManager
        )
    }

    override fun launchMainApp() {
        val intent = Intent(this, com.shareconnect.qbitconnect.ui.MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}