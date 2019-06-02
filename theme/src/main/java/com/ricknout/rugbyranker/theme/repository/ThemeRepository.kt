package com.ricknout.rugbyranker.theme.repository

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.BuildCompat
import com.ricknout.rugbyranker.theme.prefs.ThemeSharedPreferences
import com.ricknout.rugbyranker.theme.vo.Theme

class ThemeRepository(
    private val themeSharedPreferences: ThemeSharedPreferences
) {

    fun getThemeMode() = themeSharedPreferences.getThemeMode(getDefaultThemeMode())

    fun setThemeMode(themeMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
        themeSharedPreferences.setThemeMode(themeMode)
    }

    fun setDefaultThemeMode() {
        val themeMode = getThemeMode()
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

    // TODO: Replace uses of BuildCompat.isAtLeastQ() with regular Build.VERSION check when Android Q finalized
    // https://stackoverflow.com/a/55545280

    private fun getDefaultThemeMode(): Int {
        return if (BuildCompat.isAtLeastQ()) AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM else AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
    }

    fun getThemes(): List<Theme> {
        return when {
            BuildCompat.isAtLeastQ() -> listOf(Theme.LIGHT, Theme.DARK, Theme.SYSTEM_DEFAULT)
            else -> listOf(Theme.LIGHT, Theme.DARK, Theme.SET_BY_BATTERY_SAVER)
        }
    }
}
