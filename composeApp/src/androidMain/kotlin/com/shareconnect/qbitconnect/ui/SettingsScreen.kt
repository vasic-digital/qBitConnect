package com.shareconnect.qbitconnect.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.shareconnect.qbitconnect.data.Theme
import com.shareconnect.qbitconnect.ui.viewmodels.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    // Use local state for now to avoid potential injection issues
    val themeState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(Theme.SYSTEM_DEFAULT) }
    val enableDynamicColorsState = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(true) }

    val theme = themeState.value
    val enableDynamicColors = enableDynamicColorsState.value

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
                    onClick = { themeState.value = Theme.SYSTEM_DEFAULT }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("System Default")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = theme == Theme.LIGHT,
                    onClick = { themeState.value = Theme.LIGHT }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Light")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = theme == Theme.DARK,
                    onClick = { themeState.value = Theme.DARK }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Dark")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = enableDynamicColors,
                    onCheckedChange = { enableDynamicColorsState.value = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enable Dynamic Colors")
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