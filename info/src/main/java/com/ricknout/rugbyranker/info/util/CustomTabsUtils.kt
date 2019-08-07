package com.ricknout.rugbyranker.info.util

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.ricknout.rugbyranker.info.R
import com.ricknout.rugbyranker.theme.vo.Theme

object CustomTabsUtils {

    fun launchCustomTab(context: Context, url: String, theme: Theme) {
        val lightToolbarColor = ContextCompat.getColor(context, R.color.world_rugby_green)
        val darkToolbarColor = ContextCompat.getColor(context, R.color.dark_grey)
        val darkParams = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(darkToolbarColor)
                .build()
        val colorScheme = when (theme) {
            Theme.LIGHT -> CustomTabsIntent.COLOR_SCHEME_LIGHT
            Theme.DARK -> CustomTabsIntent.COLOR_SCHEME_DARK
            else -> CustomTabsIntent.COLOR_SCHEME_SYSTEM
        }
        CustomTabsIntent.Builder()
                .setToolbarColor(lightToolbarColor)
                .setColorScheme(colorScheme)
                .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, darkParams)
                .enableUrlBarHiding()
                .setShowTitle(true)
                .addDefaultShareMenuItem()
                .build()
                .launchUrl(context, Uri.parse(url))
    }
}
