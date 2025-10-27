/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.qbitconnect.plugin.language

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class LanguagePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val localesConfigTask = project.tasks.register<GenerateLocalesConfigTask>("generateLocalesConfig") {
            group = "other"
            description = "Generates the locales configuration file"
        }

        val languageListTask = project.tasks.register<GenerateLanguageListTask>("generateLanguageList") {
            group = "other"
            description = "Generates the language list file"
        }

        val copyLanguagesToAndroidTask = project.tasks.register<CopyLanguagesToAndroidTask>("copyLanguagesToAndroid") {
            group = "other"
            description = "Copies the strings files to the Android project"
        }

        project.tasks.getByName("preBuild").dependsOn(localesConfigTask, languageListTask, copyLanguagesToAndroidTask)
        project.afterEvaluate {
            project.tasks.getByName("compileKotlinDesktop").dependsOn(languageListTask)
        }
    }
}

