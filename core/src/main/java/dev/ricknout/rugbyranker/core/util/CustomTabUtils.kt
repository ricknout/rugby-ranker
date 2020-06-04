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
        val darkParams = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(darkToolbarColor)
            .build()
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
