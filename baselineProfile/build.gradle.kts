@file:Suppress("UnstableApiUsage")

import org.gradle.api.JavaVersion

plugins {

    id("com.android.test") version "8.13.0"
    id("androidx.baselineprofile") version "1.3.4"
}

android {

    namespace = "com.shareconnect.qbitconnect.baselineprofile"
    compileSdk = 36

    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    defaultConfig {

        minSdk = 28
        targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR"
    }

    targetProjectPath = ":Connectors:qBitConnect:composeApp"

    flavorDimensions += listOf("firebase")

    productFlavors {

        create("free") {

            dimension = "firebase"
        }

        create("firebase") {

            dimension = "firebase"
        }
    }
}

// baselineProfile {
//
//     useConnectedDevices = true
// }

dependencies {

    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
}

// androidComponents {
//
//     onVariants { variant ->
//
//         val artifactsLoader = variant.artifacts.getBuiltArtifactsLoader()
//
//         variant.instrumentationRunnerArguments.put(
//
//             "targetAppId",
//             variant.testedApks.map { artifactsLoader.load(it)?.applicationId!! }
//         )
//     }
// }
