package com.shareconnect.qbitconnect.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if onboarding is needed BEFORE setting up UI
        if (isOnboardingNeeded()) {
            // Finish this activity immediately - onboarding will be launched from App
            finish()
            return
        }

        setContent {
            App()
        }
    }

    private fun isOnboardingNeeded(): Boolean {
        val prefs = getSharedPreferences("onboarding_prefs", MODE_PRIVATE)
        return !prefs.getBoolean("onboarding_completed", false)
    }
}