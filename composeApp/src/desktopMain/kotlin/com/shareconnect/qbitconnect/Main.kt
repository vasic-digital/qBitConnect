package com.shareconnect.qbitconnect

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.configureSwingGlobalsForCompose
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.shareconnect.qbitconnect.data.ConfigMigrator
import com.shareconnect.qbitconnect.data.DesktopSettingsManager
import com.shareconnect.qbitconnect.di.appModule
import com.shareconnect.qbitconnect.generated.BuildConfig
import com.shareconnect.qbitconnect.model.WindowState
import com.shareconnect.qbitconnect.network.UpdateChecker
import com.shareconnect.qbitconnect.network.VersionInfo
import com.shareconnect.qbitconnect.ui.components.Dialog
import com.shareconnect.qbitconnect.ui.main.MainScreen
import com.shareconnect.qbitconnect.ui.theme.AppTheme
import com.shareconnect.qbitconnect.utils.Platform
import com.shareconnect.qbitconnect.utils.currentPlatform
import com.shareconnect.qbitconnect.utils.rememberReplaceAndApplyStyle
import com.shareconnect.qbitconnect.utils.stringResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.core.context.startKoin
import qbitconnect.composeapp.generated.resources.Res
import qbitconnect.composeapp.generated.resources.app_name
import qbitconnect.composeapp.generated.resources.dialog_cancel
import qbitconnect.composeapp.generated.resources.icon_rounded
import qbitconnect.composeapp.generated.resources.update_dialog_download
import qbitconnect.composeapp.generated.resources.update_dialog_message
import qbitconnect.composeapp.generated.resources.update_dialog_title
import java.awt.Color
import java.awt.Dimension
import java.util.Locale
import androidx.compose.ui.text.intl.Locale as ComposeLocale

fun main() {
    if (currentPlatform is Platform.Desktop.MacOS) {
        System.setProperty("apple.awt.application.appearance", "system")
    }

    configureSwingGlobalsForCompose()

    val koinApplication = startKoin {
        modules(appModule)
    }
    val koin = koinApplication.koin

    val configMigrator = koin.get<ConfigMigrator>()
    configMigrator.run()

    val updateChecker = koin.get<UpdateChecker>()
    val settingsManager = koin.get<DesktopSettingsManager>()
    if (BuildConfig.EnableUpdateChecker) {
        CoroutineScope(Dispatchers.Default).launch {
            settingsManager.checkUpdates.flow.collectLatest { enabled ->
                if (enabled) {
                    updateChecker.start()
                } else {
                    updateChecker.stop()
                }
            }
        }
    }

    val savedWindowState = settingsManager.windowState.value
    application {
        val windowState = rememberWindowState(
            placement = savedWindowState.placement,
            position = savedWindowState.position,
            size = savedWindowState.size,
        )
        LaunchedEffect(windowState) {
            snapshotFlow { WindowState(windowState.placement, windowState.position, windowState.size) }
                .debounce(200)
                .collect { settingsManager.windowState.value = it }
        }

        val language by settingsManager.language.flow.collectAsState()
        val defaultLanguage = remember { Locale.getDefault() }
        LaunchedEffect(language) {
            val locale = if (language.isNotEmpty()) {
                ComposeLocale(language).platformLocale
            } else {
                defaultLanguage
            }
            Locale.setDefault(locale)
        }

        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(Res.string.app_name),
            icon = painterResource(Res.drawable.icon_rounded),
            state = windowState,
        ) {
            if (currentPlatform is Platform.Desktop.Windows) {
                val density = LocalDensity.current
                LaunchedEffect(density) {
                    window.minimumSize = with(density) {
                        Dimension(420.dp.toPx().toInt(), 360.dp.toPx().toInt())
                    }
                }
            } else {
                LaunchedEffect(Unit) {
                    window.minimumSize = Dimension(420, 360)
                }
            }

            AppTheme {
                val backgroundColor = MaterialTheme.colorScheme.background
                LaunchedEffect(backgroundColor) {
                    window.background = Color(backgroundColor.toArgb())
                }

                var newVersionInfo: VersionInfo? by remember { mutableStateOf(null) }
                if (newVersionInfo != null) {
                    Dialog(
                        onDismissRequest = { newVersionInfo = null },
                        title = { Text(text = stringResource(Res.string.update_dialog_title)) },
                        text = {
                            Text(
                                text = rememberReplaceAndApplyStyle(
                                    text = stringResource(Res.string.update_dialog_message),
                                    oldValues = listOf("%1\$s", "%2\$s"),
                                    newValues = listOf(newVersionInfo?.version ?: "", BuildConfig.Version),
                                    styles = listOf(
                                        SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold),
                                        SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold),
                                    ),
                                ),
                            )
                        },
                        confirmButton = {
                            val uriHandler = LocalUriHandler.current
                            Button(
                                onClick = {
                                    try {
                                        uriHandler.openUri(newVersionInfo?.url ?: "")
                                    } catch (_: Exception) {
                                    }

                                    newVersionInfo = null
                                },
                            ) {
                                Text(text = stringResource(Res.string.update_dialog_download))
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { newVersionInfo = null },
                            ) {
                                Text(text = stringResource(Res.string.dialog_cancel))
                            }
                        },
                    )
                }

                LaunchedEffect(updateChecker.updateFlow) {
                    updateChecker.updateFlow.collectLatest { version ->
                        newVersionInfo = version
                    }
                }

                MainScreen()
            }
        }
    }
}
