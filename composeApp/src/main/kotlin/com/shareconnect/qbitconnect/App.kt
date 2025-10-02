package com.shareconnect.qbitconnect

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Manual DI initialization is handled in androidMain
    }
}
