package dev.ricknout.rugbyranker.theme.data

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import dev.ricknout.rugbyranker.theme.model.Theme

class ThemeRepository(private val sharedPreferences: ThemeSharedPreferences) {

    fun getTheme(): Theme {
        val defaultTheme = getDefaultTheme()
        val defaultMode = defaultTheme.mode
        val mode = sharedPreferences.getMode(defaultMode)
        val themes = getThemes()
        return themes.find { theme -> theme.mode == mode } ?: defaultTheme
    }

    fun setTheme(theme: Theme) {
        val mode = theme.mode
        AppCompatDelegate.setDefaultNightMode(mode)
        sharedPreferences.setMode(mode)
    }

    fun setTheme() {
        val theme = getTheme()
        val mode = theme.mode
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun getDefaultTheme(): Theme {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Theme.SYSTEM_DEFAULT
            else -> Theme.SET_BY_BATTERY_SAVER
        }
    }

    fun getThemes(): List<Theme> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> listOf(
                Theme.LIGHT,
                Theme.DARK,
                Theme.SYSTEM_DEFAULT
            )
            else -> listOf(Theme.LIGHT, Theme.DARK, Theme.SET_BY_BATTERY_SAVER)
        }
    }
}
