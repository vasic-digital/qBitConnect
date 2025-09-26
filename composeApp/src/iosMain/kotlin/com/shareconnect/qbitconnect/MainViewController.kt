package com.shareconnect.qbitconnect

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import com.shareconnect.qbitconnect.data.ConfigMigrator
import com.shareconnect.qbitconnect.di.appModule
import com.shareconnect.qbitconnect.ui.main.MainScreen
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@Suppress("ktlint:standard:function-naming", "unused", "FunctionName")
fun MainViewController() = ComposeUIViewController {
    KoinApplication(
        application = {
            modules(appModule)
        },
    ) {
        val configMigrator = koinInject<ConfigMigrator>()
        var ranConfigMigration by remember { mutableStateOf(false) }
        if (!ranConfigMigration) {
            configMigrator.run()
            ranConfigMigration = true
        }

        MainScreen()
    }
}
