package dev.ricknout.rugbyranker.theme.data

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import dev.ricknout.rugbyranker.theme.model.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ThemeRepository(private val dataStore: ThemeDataStore) {
    fun getTheme(): Flow<Theme> {
        val defaultTheme = getDefaultTheme()
        val defaultMode = defaultTheme.mode
        val themes = getThemes()
        return dataStore.getMode(defaultMode).map { mode ->
            themes.find { theme -> theme.mode == mode } ?: defaultTheme
        }
    }

    fun setTheme(
        theme: Theme,
        coroutineScope: CoroutineScope,
    ) {
        val mode = theme.mode
        AppCompatDelegate.setDefaultNightMode(mode)
        coroutineScope.launch {
            dataStore.setMode(mode)
        }
    }

    fun setTheme(coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            val theme = getTheme().first()
            withContext(Dispatchers.Main) {
                val mode = theme.mode
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }
    }

    private fun getDefaultTheme(): Theme {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> Theme.SYSTEM_DEFAULT
            else -> Theme.SET_BY_BATTERY_SAVER
        }
    }

    fun getThemes(): List<Theme> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                listOf(
                    Theme.LIGHT,
                    Theme.DARK,
                    Theme.SYSTEM_DEFAULT,
                )
            else -> listOf(Theme.LIGHT, Theme.DARK, Theme.SET_BY_BATTERY_SAVER)
        }
    }
}
