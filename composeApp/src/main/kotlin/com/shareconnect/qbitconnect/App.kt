package com.shareconnect.qbitconnect

import android.app.Application
import com.shareconnect.qbitconnect.data.SettingsManager
import com.shareconnect.qbitconnect.data.repositories.RSSRepository
import com.shareconnect.qbitconnect.data.repositories.SearchRepository
import com.shareconnect.qbitconnect.data.repositories.ServerRepository
import com.shareconnect.qbitconnect.data.repositories.TorrentRepository
import com.shareconnect.qbitconnect.ui.viewmodels.AddTorrentViewModel
import com.shareconnect.qbitconnect.ui.viewmodels.RSSViewModel
import com.shareconnect.qbitconnect.ui.viewmodels.SearchViewModel
import com.shareconnect.qbitconnect.ui.viewmodels.ServerListViewModel
import com.shareconnect.qbitconnect.ui.viewmodels.SettingsViewModel
import com.shareconnect.qbitconnect.ui.viewmodels.TorrentListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Koin for dependency injection
        startKoin {
            androidContext(this@App)
            modules(
                module {
                    single { SettingsManager(get()) }
                    single { ServerRepository() }
                    single { TorrentRepository() }
                    single { RSSRepository() }
                    single { SearchRepository() }

                    viewModel { ServerListViewModel(get()) }
                    viewModel { TorrentListViewModel(get(), get()) }
                    viewModel { AddTorrentViewModel(get(), get()) }
                    viewModel { RSSViewModel(get(), get()) }
                    viewModel { SearchViewModel(get(), get()) }
                    viewModel { SettingsViewModel(get()) }
                }
            )
        }
    }
}
