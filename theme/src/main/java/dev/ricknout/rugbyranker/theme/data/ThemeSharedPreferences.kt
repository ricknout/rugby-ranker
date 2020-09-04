package dev.ricknout.rugbyranker.theme.data

import android.content.SharedPreferences
import androidx.core.content.edit

class ThemeSharedPreferences(private val sharedPreferences: SharedPreferences) {

    fun setMode(mode: Int) = sharedPreferences.edit { putInt(KEY_MODE, mode) }

    fun getMode(defaultMode: Int) = sharedPreferences.getInt(KEY_MODE, defaultMode)

    companion object {
        private const val KEY_MODE = "mode"
    }
}
