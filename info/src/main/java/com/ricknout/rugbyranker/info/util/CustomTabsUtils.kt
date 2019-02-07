package com.ricknout.rugbyranker.info.util

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.ricknout.rugbyranker.core.util.getColorPrimary

object CustomTabsUtils {

    fun launchCustomTab(context: Context, url: String) {
        val colorPrimary = context.getColorPrimary()
        CustomTabsIntent.Builder()
                .setToolbarColor(colorPrimary)
                .enableUrlBarHiding()
                .setShowTitle(true)
                .addDefaultShareMenuItem()
                .build()
                .launchUrl(context, Uri.parse(url))
    }
}
