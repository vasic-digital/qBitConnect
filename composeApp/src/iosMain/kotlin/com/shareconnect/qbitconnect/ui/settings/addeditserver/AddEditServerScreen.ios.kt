package com.shareconnect.qbitconnect.ui.settings.addeditserver

import platform.Foundation.NSURL

actual fun isPlatformUrlValid(url: String) = NSURL.URLWithString(url) != null
