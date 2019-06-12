package com.ricknout.rugbyranker.theme.repository

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.ricknout.rugbyranker.theme.prefs.ThemeSharedPreferences
import com.ricknout.rugbyranker.theme.vo.Theme

class ThemeRepository(
    private val themeSharedPreferences: ThemeSharedPreferences
) {

    fun getTheme(): Theme {
        val defaultTheme = getDefaultTheme()
        val defaultThemeMode = defaultTheme.mode
        val themeMode = themeSharedPreferences.getThemeMode(defaultThemeMode)
        val themes = getThemes()
        return themes.find { theme -> theme.mode == themeMode } ?: defaultTheme
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

    private fun getDefaultTheme(): Theme {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Theme.SYSTEM_DEFAULT
            else -> Theme.SET_BY_BATTERY_SAVER
        }
    }

    fun getThemes(): List<Theme> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> listOf(Theme.LIGHT, Theme.DARK, Theme.SYSTEM_DEFAULT)
            else -> listOf(Theme.LIGHT, Theme.DARK, Theme.SET_BY_BATTERY_SAVER)
        }
    }
}
