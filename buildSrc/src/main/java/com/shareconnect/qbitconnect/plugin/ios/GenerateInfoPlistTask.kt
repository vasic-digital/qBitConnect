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


package com.shareconnect.qbitconnect.plugin.ios

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class GenerateInfoPlistTask : DefaultTask() {

    private val emptyResourcesElement = "<resources>\\s*</resources>|<resources/>".toRegex()
    private val valuesPrefix = "values(-(b\\+)?)?".toRegex()

    @get:Input
    val languages = project.fileTree("${project.projectDir}/src/commonMain/composeResources/")
        .matching { include("**/strings.xml") }
        .filterNot { it.readText().contains(emptyResourcesElement) }
        .mapNotNull { it.parentFile.name }
        .sorted()
        .map {
            it.replaceFirst(valuesPrefix, "")
                .replace("-r", "-")
                .replace("+", "-")
                .takeIf(String::isNotBlank) ?: "en"
        }

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        outputFile.convention(project.layout.projectDirectory.file("${project.rootDir}/iosApp/iosApp/Info.plist"))
    }

    @TaskAction
    fun generateInfoPlist() {
        val localizations = buildString {
            appendLine("<key>CFBundleLocalizations</key>")
            appendLine("<array>")
            languages.forEach {
                appendLine("    <string>$it</string>")
            }
            appendLine("</array>")
        }.prependIndent("    ")

        val infoPlistContent = javaClass.classLoader.getResourceAsStream("Info.plist")!!
            .readAllBytes()
            .toString(Charsets.UTF_8)
            .replace("<!-- {{LOCALIZATIONS}} -->", localizations)

        val file = outputFile.get().asFile
        file.writeText(infoPlistContent)
    }
}
