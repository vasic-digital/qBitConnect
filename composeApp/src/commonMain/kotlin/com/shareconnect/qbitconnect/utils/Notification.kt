package com.shareconnect.qbitconnect.utils

import androidx.compose.runtime.Composable

@Composable
expect fun notificationPermissionLauncher(): (() -> Unit)?

@Composable
expect fun areNotificationsEnabled(): Boolean?
