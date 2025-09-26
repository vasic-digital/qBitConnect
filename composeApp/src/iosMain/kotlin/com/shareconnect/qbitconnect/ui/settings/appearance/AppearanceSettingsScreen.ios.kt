package com.shareconnect.qbitconnect.ui.settings.appearance

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.shareconnect.qbitconnect.preferences.Preference
import com.shareconnect.qbitconnect.utils.stringResource
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import qbitcontroller.composeapp.generated.resources.Res
import qbitcontroller.composeapp.generated.resources.settings_language

@Composable
actual fun LanguagePreference() {
    Preference(
        title = { Text(text = stringResource(Res.string.settings_language)) },
        onClick = {
            val url = NSURL(string = UIApplicationOpenSettingsURLString)
            if (UIApplication.sharedApplication.canOpenURL(url)) {
                UIApplication.sharedApplication.openURL(url, emptyMap<Any?, Any>(), null)
            }
        },
    )
}
