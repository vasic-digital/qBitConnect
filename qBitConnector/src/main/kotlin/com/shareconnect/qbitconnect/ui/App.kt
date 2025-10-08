package com.shareconnect.qbitconnect.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shareconnect.designsystem.compose.theme.DesignSystemTheme
import com.shareconnect.qbitconnect.data.Theme
import com.shareconnect.qbitconnect.ui.viewmodels.AppViewModel
import com.shareconnect.qbitconnect.ui.viewmodels.AppViewModelFactory

@Composable
fun App() {
    val viewModel = viewModel<AppViewModel>(
        factory = AppViewModelFactory()
    )
    val theme by viewModel.theme.collectAsState(initial = Theme.SYSTEM_DEFAULT)
    val enableDynamicColors by viewModel.enableDynamicColors.collectAsState(initial = true)

    val isDarkTheme = when (theme) {
        Theme.DARK -> true
        Theme.LIGHT -> false
        Theme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
        else -> isSystemInDarkTheme()
    }

    DesignSystemTheme(darkTheme = isDarkTheme) {
        Box(modifier = Modifier.testTag("main_screen")) {
            AppNavigation()
        }
    }
}