@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalEncodingApi::class)

import android.databinding.tool.ext.joinToCamelCaseAsVar
import org.gradle.api.JavaVersion
import org.gradle.internal.os.OperatingSystem
// import org.jetbrains.compose.desktop.application.dsl.TargetFormat // Not needed for Android
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jmailen.gradle.kotlinter.tasks.ConfigurableKtLintTask
import java.io.FileInputStream
import java.util.Locale
import java.util.Properties
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

plugins {

    id("com.android.application") version "8.13.0"
    id("org.jetbrains.kotlin.android") version "2.0.21"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
}

// buildConfig {
//
//     packageName("com.shareconnect.qbitconnect.generated")
//
//     buildConfigField("Version", "1.0.0")
//     buildConfigField("SourceCodeUrl", "https://github.com/vasic-digital/qBitConnect")
//
//     // Desktop only
//     buildConfigField("EnableUpdateChecker", true)
//     buildConfigField("LatestReleaseUrl", "https://api.github.com/repos/vasic-digital/qBitConnect/releases/latest")
// }



android {

    namespace = "com.shareconnect.qbitconnect"
    compileSdk = 36

    defaultConfig {

        applicationId = "com.shareconnect.qbitconnect"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        release {

            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        debug {

            isMinifyEnabled = false
            isShrinkResources = false
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {

        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {

        jvmTarget = "21"

        freeCompilerArgs += listOf(

            "-opt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi",
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-opt-in=kotlin.time.ExperimentalTime"
        )
    }

    buildFeatures {

        compose = true
    }

    lint {

        disable += listOf("MissingTranslation", "ExtraTranslation")
    }

    dependenciesInfo {

        includeInApk = false
        includeInBundle = false
    }
}

dependencies {

    implementation(platform("androidx.compose:compose-bom:2025.09.00"))
    coreLibraryDesugaring(libs.desugar)
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation(libs.androidx.lifecycle.viewModel)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.compose.material3:material3-window-size-class")

    implementation(libs.compose.navigation)

    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    implementation(libs.kotlinxSerialization)
    implementation(libs.kotlinx.datetime)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.viewModel)
    implementation(libs.koin.compose.viewModel.navigation)
    implementation(libs.koin.androidx.workManager)

    implementation(libs.ktor.core)
    implementation(libs.ktor.okhttp)
    implementation(libs.ktor.contentNegotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.auth)

    implementation(project.dependencies.platform(libs.okhttp.bom))
    implementation(libs.okhttp.doh)

    implementation(libs.coil)
    implementation(libs.coil.ktor)
    implementation(libs.coil.svg)

    implementation(libs.htmlConverter)

    implementation(project(":Connectors:qBitConnect:preferences"))

    implementation(libs.multiplatformSettings)

    implementation(libs.materialKolor)

    implementation(libs.fileKit.core)
    implementation(libs.fileKit.dialogs)

    implementation(libs.reorderable)

    implementation(libs.autolinktext)

    implementation(libs.composePipette)

    // implementation("androidx.compose.ui:ui-tooling-preview") // Already added
    implementation(libs.androidx.appcompat)

    implementation(libs.material)

    implementation(libs.androidx.profileinstaller)

    implementation(libs.accompanist.permissions)

    implementation(libs.work.runtime)
}



// afterEvaluate {
//
//     tasks.withType<ConfigurableKtLintTask> {
//
//         source = source.minus(fileTree("build")).asFileTree
//     }
// }


