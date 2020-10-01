package dev.ricknout.rugbyranker.core.util

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import dev.ricknout.rugbyranker.core.R

object CustomTabUtils {

    fun launchCustomTab(context: Context, url: String, @CustomTabsIntent.ColorScheme colorScheme: Int) {
        val lightToolbarColor = ContextCompat.getColor(context, R.color.white)
        val darkToolbarColor = ContextCompat.getColor(context, R.color.dark_grey)
        val lightParams = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(lightToolbarColor)
            .build()
        val darkParams = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(darkToolbarColor)
            .build()
        CustomTabsIntent.Builder()
            .setColorScheme(colorScheme)
            .setDefaultColorSchemeParams(lightParams)
            .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, darkParams)
            .setUrlBarHidingEnabled(true)
            .setShowTitle(true)
            .setShareState(CustomTabsIntent.SHARE_STATE_ON)
            .build()
            .launchUrl(context, Uri.parse(url))
    }
}
