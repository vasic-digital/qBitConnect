@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalEncodingApi::class)

import android.databinding.tool.ext.joinToCamelCaseAsVar
import org.gradle.api.JavaVersion
import org.gradle.api.tasks.testing.Test
import org.gradle.internal.os.OperatingSystem
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jmailen.gradle.kotlinter.tasks.ConfigurableKtLintTask
import java.io.FileInputStream
import java.util.Locale
import java.util.Properties
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

plugins {
    id("com.android.library") version "8.13.0"
    id("org.jetbrains.kotlin.android") version "2.2.20"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.20"
}

android {
    namespace = "com.shareconnect.qbitconnect.shared"
    compileSdk = 36

    defaultConfig {

        minSdk = 21
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
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"

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

    testOptions {
        unitTests.all {
            it.jvmArgs("-XX:+AllowRedefinitionToAddDeleteMethods")
        }
    }

    tasks.withType<Test> {
        useJUnit()
        javaLauncher.set(javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(17))
        })
    }

    lint {

        disable += listOf("MissingTranslation", "ExtraTranslation")
    }
}

dependencies {
    val androidxLifecycleVersion = "2.9.1"
    val composeNavigationVersion = "2.9.0-beta03"
    val coroutinesVersion = "1.10.2"
    val kotlinxSerializationVersion = "1.9.0"
    val kotlinxDatetimeVersion = "0.7.1"
    val koinVersion = "4.1.0"
    val ktorVersion = "3.2.3"
    val okhttpVersion = "5.1.0"
    val coilVersion = "3.3.0"
    val htmlConverterVersion = "1.1.0"
    val multiplatformSettingsVersion = "1.3.0"
    val materialKolorVersion = "2.1.1"
    val fileKitVersion = "0.10.0"
    val reorderableVersion = "2.5.1"
    val autolinktextVersion = "2.0.2"
    val composePipetteVersion = "1.0.1"
    val androidxAppcompatVersion = "1.7.1"
    val materialVersion = "1.12.0"
    val profileinstallerVersion = "1.4.1"
    val accompanistVersion = "0.37.3"
    val workRuntimeVersion = "2.10.2"

    implementation(platform("androidx.compose:compose-bom:2025.09.00"))
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel:$androidxLifecycleVersion")
    implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:$androidxLifecycleVersion")
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.activity:activity-compose:1.11.0")
    implementation("androidx.compose.material3:material3-window-size-class")

    implementation("org.jetbrains.androidx.navigation:navigation-compose:$composeNavigationVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")

    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-android:$koinVersion")
    implementation("io.insert-koin:koin-compose:$koinVersion")
    implementation("io.insert-koin:koin-compose-viewmodel:$koinVersion")
    implementation("io.insert-koin:koin-compose-viewmodel-navigation:$koinVersion")
    implementation("io.insert-koin:koin-androidx-workmanager:$koinVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")

    implementation(project.dependencies.platform("com.squareup.okhttp3:okhttp-bom:$okhttpVersion"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.squareup.okhttp3:okhttp-dnsoverhttps")

    implementation("io.coil-kt.coil3:coil-compose:$coilVersion")
    implementation("io.coil-kt.coil3:coil-network-ktor3:$coilVersion")
    implementation("io.coil-kt.coil3:coil-svg:$coilVersion")

    implementation("be.digitalia.compose.htmlconverter:htmlconverter:$htmlConverterVersion")

    implementation("com.russhwolf:multiplatform-settings:$multiplatformSettingsVersion")

    implementation("com.materialkolor:material-kolor:$materialKolorVersion")

    implementation("io.github.vinceglb:filekit-core:$fileKitVersion")
    implementation("io.github.vinceglb:filekit-dialogs:$fileKitVersion")

    implementation("sh.calvin.reorderable:reorderable:$reorderableVersion")

    implementation("sh.calvin.autolinktext:autolinktext:$autolinktextVersion")

    implementation("dev.zt64.compose.pipette:compose-pipette:$composePipetteVersion")

    implementation("androidx.appcompat:appcompat:$androidxAppcompatVersion")

    implementation("com.google.android.material:material:$materialVersion")

    implementation("androidx.profileinstaller:profileinstaller:$profileinstallerVersion")

    implementation("com.google.accompanist:accompanist-permissions:$accompanistVersion")

    implementation("androidx.work:work-runtime-ktx:$workRuntimeVersion")

    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("com.russhwolf:multiplatform-settings-test:$multiplatformSettingsVersion")

    // Android test dependencies
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("org.mockito:mockito-android:5.12.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
}