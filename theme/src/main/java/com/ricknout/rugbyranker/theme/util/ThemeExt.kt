package com.ricknout.rugbyranker.theme.util

import androidx.browser.customtabs.CustomTabsIntent
import com.ricknout.rugbyranker.theme.vo.Theme

@CustomTabsIntent.ColorScheme
fun Theme.getCustomTabsIntentColorScheme() = when (this) {
    Theme.LIGHT -> CustomTabsIntent.COLOR_SCHEME_LIGHT
    Theme.DARK -> CustomTabsIntent.COLOR_SCHEME_DARK
    else -> CustomTabsIntent.COLOR_SCHEME_SYSTEM
}
