package com.shareconnect.qbitconnect.ui.settings.addeditserver

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

actual fun isPlatformUrlValid(url: String) = url.toHttpUrlOrNull() != null
