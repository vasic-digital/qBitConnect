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

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.Locale

@CacheableTask
abstract class GenerateLanguageListTask : DefaultTask() {

    private val emptyResourcesElement = "<resources>\\s*</resources>|<resources/>".toRegex()
    private val valuesPrefix = "values(-(b\\+)?)?".toRegex()

    @get:Input
    val languages = project.fileTree("${project.projectDir}/src/commonMain/composeResources/")
        .matching { include("**/strings.xml") }
        .filterNot { it.readText().contains(emptyResourcesElement) }
        .mapNotNull { it.parentFile.name }
        .map {
            it.replaceFirst(valuesPrefix, "")
                .replace("-r", "-")
                .replace("+", "-")
                .takeIf(String::isNotBlank) ?: "en"
        }
        .sorted()

    @get:OutputFile
    val languagesKtFile: File =
        project.layout.buildDirectory.file("generated/kotlin/com/shareconnect/qbitconnect/generated/Languages.kt")
            .get().asFile

    @TaskAction
    fun generateLanguageListTask() {
        val content = buildString {
            appendLine("package com.shareconnect.qbitconnect.generated")
            appendLine()
            appendLine("val SupportedLanguages = mapOf(")
            languages.forEachIndexed { _, lang ->
                appendLine("""    "$lang" to "${getLanguageDisplayName(lang)}",""")
            }
            appendLine(")")
        }

        languagesKtFile.parentFile.mkdirs()
        languagesKtFile.writeText(content)
    }

    private fun getLanguageDisplayName(language: String): String? {
        val locale = when (language) {
            "zh-CN" -> Locale.forLanguageTag("zh-Hans")
            "zh-TW" -> Locale.forLanguageTag("zh-Hant")
            else -> Locale.forLanguageTag(language)
        }
        return locale.getDisplayName(locale).replaceFirstChar { it.uppercase() }
    }
}
