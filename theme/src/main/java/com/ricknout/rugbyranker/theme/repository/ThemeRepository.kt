package com.ricknout.rugbyranker.theme.repository

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.BuildCompat
import com.ricknout.rugbyranker.theme.prefs.ThemeSharedPreferences
import com.ricknout.rugbyranker.theme.vo.Theme

class ThemeRepository(
    private val themeSharedPreferences: ThemeSharedPreferences
) {

    fun getTheme(): Theme {
        val defaultThemeMode = getDefaultTheme().mode
        val themeMode = themeSharedPreferences.getThemeMode(defaultThemeMode)
        return Theme.values().find { theme -> theme.mode == themeMode } ?: throw RuntimeException("No Theme found for mode: $themeMode")
    }

    fun setTheme(theme: Theme) {
        val themeMode = theme.mode
        AppCompatDelegate.setDefaultNightMode(themeMode)
        themeSharedPreferences.setThemeMode(themeMode)
    }

    fun setDefaultTheme() {
        val theme = getTheme()
        val themeMode = theme.mode
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

    // TODO: Replace uses of BuildCompat.isAtLeastQ() with regular Build.VERSION check when Android Q finalized
    // https://stackoverflow.com/a/55545280

    private fun getDefaultTheme(): Theme {
        return when {
            BuildCompat.isAtLeastQ() -> Theme.SYSTEM_DEFAULT
            else -> Theme.SET_BY_BATTERY_SAVER
        }
    }

    fun getThemes(): List<Theme> {
        return when {
            BuildCompat.isAtLeastQ() -> listOf(Theme.LIGHT, Theme.DARK, Theme.SYSTEM_DEFAULT)
            else -> listOf(Theme.LIGHT, Theme.DARK, Theme.SET_BY_BATTERY_SAVER)
        }
    }
}
