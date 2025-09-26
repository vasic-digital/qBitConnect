package com.shareconnect.qbitconnect.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.backhandler.BackHandler
import com.shareconnect.qbitconnect.utils.Platform
import com.shareconnect.qbitconnect.utils.currentPlatform

@Composable
fun PlatformBackHandler(enabled: Boolean = true, onBack: () -> Unit) {
    if (currentPlatform != Platform.Mobile.IOS) {
        BackHandler(
            enabled = enabled,
            onBack = onBack,
        )
    }
}
