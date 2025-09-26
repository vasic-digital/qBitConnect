package com.shareconnect.qbitconnect.ui.settings.appearance

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shareconnect.qbitconnect.data.DesktopSettingsManager
import com.shareconnect.qbitconnect.generated.SupportedLanguages
import com.shareconnect.qbitconnect.preferences.ListPreference
import com.shareconnect.qbitconnect.utils.stringResource
import org.koin.compose.koinInject
import qbitcontroller.composeapp.generated.resources.Res
import qbitcontroller.composeapp.generated.resources.settings_language
import qbitcontroller.composeapp.generated.resources.settings_language_system_default

@Composable
actual fun LanguagePreference() {
    val settingsManger = koinInject<DesktopSettingsManager>()
    val language by settingsManger.language.flow.collectAsStateWithLifecycle()

    val locales = mapOf("" to stringResource(Res.string.settings_language_system_default)) + SupportedLanguages
    ListPreference(
        value = language,
        onValueChange = { settingsManger.language.value = it },
        values = locales.keys.toList(),
        title = { Text(text = stringResource(Res.string.settings_language)) },
        summary = locales[language]?.let {
            {
                Text(text = it)
            }
        },
        valueToText = { AnnotatedString(locales[it] ?: "") },
    )
}
