package com.shareconnect.qbitconnect.utils

import androidx.compose.ui.platform.ClipEntry

actual fun String.toClipEntry() = ClipEntry.withPlainText(this)
