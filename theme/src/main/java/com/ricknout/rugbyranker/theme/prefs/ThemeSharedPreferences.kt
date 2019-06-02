package com.ricknout.rugbyranker.theme.prefs

import android.content.SharedPreferences
import androidx.core.content.edit

class ThemeSharedPreferences(private val sharedPreferences: SharedPreferences) {

    fun setThemeMode(themeMode: Int) = sharedPreferences.edit { putInt(KEY_THEME_MODE, themeMode) }

    fun getThemeMode(defaultThemeMode: Int) = sharedPreferences.getInt(KEY_THEME_MODE, defaultThemeMode)

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
    }
}
