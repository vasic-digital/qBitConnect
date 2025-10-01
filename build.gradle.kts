import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {

    id("org.jmailen.kotlinter") version "5.2.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21" apply false
    id("com.github.gmazzo.buildconfig") version "5.6.7" apply false
    id("com.github.ben-manes.versions") version "0.52.0"

    id("androidx.baselineprofile") version "1.3.4" apply false
}

tasks.withType<DependencyUpdatesTask> {

    gradleReleaseChannel = "current"

    rejectVersionIf {

        fun String.isStableVersion(): Boolean {

            val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { contains(it, ignoreCase = true) }
            val regex = "^[0-9,.v-]+(-r)?$".toRegex()
            return stableKeyword || regex.matches(this)
        }

        candidate.version.isStableVersion() != currentVersion.isStableVersion()
    }
}

tasks.register<Delete>("clean") {

    delete(layout.buildDirectory)
}
