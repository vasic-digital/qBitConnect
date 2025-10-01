package com.shareconnect.qbitconnect.di

import android.content.Context
import com.russhwolf.settings.SharedPreferencesSettings
import com.shareconnect.qbitconnect.data.ServerManager
import com.shareconnect.qbitconnect.data.SettingsManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    includes(platformModule)

    single { SettingsManager(get(named("settings"))) }
    single { ServerManager(get(named("servers"))) }
}

val platformModule: Module = module {
    single(named("settings")) {
        SharedPreferencesSettings.Factory(get<Context>()).create("settings")
    }
    single(named("servers")) {
        SharedPreferencesSettings.Factory(get<Context>()).create("servers")
    }
}