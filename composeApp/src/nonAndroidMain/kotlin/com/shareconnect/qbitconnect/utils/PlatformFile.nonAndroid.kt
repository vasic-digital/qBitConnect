package com.shareconnect.qbitconnect.utils

import io.github.vinceglb.filekit.PlatformFile

actual fun String.toPlatformFile() = PlatformFile(this)
