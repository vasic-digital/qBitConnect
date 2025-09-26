package com.shareconnect.qbitconnect.utils

import androidx.compose.ui.platform.ClipEntry
import java.awt.datatransfer.StringSelection

actual fun String.toClipEntry() = ClipEntry(StringSelection(this))
