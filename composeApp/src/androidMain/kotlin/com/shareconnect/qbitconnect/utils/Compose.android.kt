package com.shareconnect.qbitconnect.utils

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

@Composable
actual fun calculateWindowSizeClass() = calculateWindowSizeClass(LocalActivity.current!!)
