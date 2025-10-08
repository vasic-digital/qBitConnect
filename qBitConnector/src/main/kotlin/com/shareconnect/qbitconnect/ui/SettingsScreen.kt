package com.shareconnect.qbitconnect.ui

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.shareconnect.qbitconnect.data.Theme
import com.shareconnect.qbitconnect.di.DependencyContainer
import com.shareconnect.qbitconnect.ui.viewmodels.SettingsViewModel
import com.shareconnect.qbitconnect.ui.viewmodels.SettingsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val viewModel = viewModel<SettingsViewModel>(
        factory = SettingsViewModelFactory(application)
    )
    val theme by viewModel.theme.collectAsState(initial = Theme.SYSTEM_DEFAULT)
    val enableDynamicColors by viewModel.enableDynamicColors.collectAsState(initial = true)
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val availableLanguages by viewModel.availableLanguages.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = theme == Theme.SYSTEM_DEFAULT,
                    onClick = { viewModel.setTheme(Theme.SYSTEM_DEFAULT) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("System Default")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = theme == Theme.LIGHT,
                    onClick = { viewModel.setTheme(Theme.LIGHT) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Light")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = theme == Theme.DARK,
                    onClick = { viewModel.setTheme(Theme.DARK) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Dark")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = enableDynamicColors,
                    onCheckedChange = { viewModel.setEnableDynamicColors(it) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enable Dynamic Colors")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Language",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Current: ${currentLanguage?.displayName ?: "Loading..."}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            availableLanguages.forEach { (code, displayName) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = currentLanguage?.languageCode == code,
                        onClick = { viewModel.setLanguage(code, displayName) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(displayName)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "About",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "qBitConnect v1.0.0",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "qBittorrent client for Android",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Back")
            }
        }
    }
}