package com.shareconnect.qbitconnect

import android.app.Application
import com.shareconnect.qbitconnect.di.DependencyContainer

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize manual dependency injection
        DependencyContainer.init(this)
    }
}
