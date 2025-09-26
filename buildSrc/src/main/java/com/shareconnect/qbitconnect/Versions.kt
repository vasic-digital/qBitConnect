package com.shareconnect.qbitconnect

import org.gradle.api.JavaVersion as GradleJavaVersion

object Versions {
    const val AppVersion = "1.0.0"
    const val AppVersionCode = 1

    object Android {

        const val CompileSdk = 36
        const val TargetSdk = 36
        const val MinSdk = 21

        val JavaVersion = GradleJavaVersion.VERSION_21
        const val JvmTarget = "21"
    }
}
