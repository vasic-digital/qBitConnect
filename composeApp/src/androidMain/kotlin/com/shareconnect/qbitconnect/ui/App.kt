package com.shareconnect.qbitconnect.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shareconnect.qbitconnect.data.Theme
import com.shareconnect.qbitconnect.di.DependencyContainer
import com.shareconnect.qbitconnect.ui.viewmodels.AppViewModel

@Composable
fun App() {
    val viewModel = viewModel<AppViewModel>(
        factory = AppViewModelFactory(DependencyContainer.settingsManager)
    )
    val theme by viewModel.theme.collectAsState(initial = Theme.SYSTEM_DEFAULT)
    val enableDynamicColors by viewModel.enableDynamicColors.collectAsState(initial = true)

    val isDarkTheme = when (theme) {
        Theme.DARK -> true
        Theme.LIGHT -> false
        Theme.SYSTEM_DEFAULT -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDarkTheme) {
        darkColorScheme(
            primary = Color(0xFF90CAF9),
            onPrimary = Color.Black,
            primaryContainer = Color(0xFF1976D2),
            onPrimaryContainer = Color.White,
            secondary = Color(0xFFCE93D8),
            onSecondary = Color.Black,
            secondaryContainer = Color(0xFF7B1FA2),
            onSecondaryContainer = Color.White,
            tertiary = Color(0xFF81C784),
            onTertiary = Color.Black,
            tertiaryContainer = Color(0xFF388E3C),
            onTertiaryContainer = Color.White,
            error = Color(0xFFEF5350),
            onError = Color.White,
            errorContainer = Color(0xFFD32F2F),
            onErrorContainer = Color.White,
            background = Color(0xFF121212),
            onBackground = Color.White,
            surface = Color(0xFF1E1E1E),
            onSurface = Color.White,
            surfaceVariant = Color(0xFF2D2D2D),
            onSurfaceVariant = Color(0xFFCCCCCC),
            outline = Color(0xFF888888)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF1976D2),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF90CAF9),
            onPrimaryContainer = Color.Black,
            secondary = Color(0xFF7B1FA2),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFCE93D8),
            onSecondaryContainer = Color.Black,
            tertiary = Color(0xFF388E3C),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFF81C784),
            onTertiaryContainer = Color.Black,
            error = Color(0xFFD32F2F),
            onError = Color.White,
            errorContainer = Color(0xFFEF5350),
            onErrorContainer = Color.White,
            background = Color.White,
            onBackground = Color.Black,
            surface = Color(0xFFFAFAFA),
            onSurface = Color.Black,
            surfaceVariant = Color(0xFFF5F5F5),
            onSurfaceVariant = Color(0xFF666666),
            outline = Color(0xFF888888)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(),
        shapes = androidx.compose.material3.Shapes()
    ) {
        Box(modifier = Modifier.testTag("main_screen")) {
            AppNavigation()
        }
    }
}