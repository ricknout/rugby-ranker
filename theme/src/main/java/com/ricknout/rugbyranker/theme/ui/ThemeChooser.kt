package com.ricknout.rugbyranker.theme.ui

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.BuildCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ricknout.rugbyranker.theme.R

fun showThemeChooser(context: Context) {
    // TODO: Replace BuildCompat.isAtLeastQ() with a regular Build.VERSION check when Android Q finalized
    // https://stackoverflow.com/a/55545280
    val themeTitles = when {
        BuildCompat.isAtLeastQ() -> R.array.themes_q
        else -> R.array.themes
    }
    val themeModes = when {
        BuildCompat.isAtLeastQ() -> listOf(AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.MODE_NIGHT_YES, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        else -> listOf(AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.MODE_NIGHT_YES, AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
    }
    var themeMode = AppCompatDelegate.getDefaultNightMode()
    val checkedItem = themeModes.indexOf(themeMode)
    MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_choose_theme)
            .setPositiveButton(R.string.button_ok) { _, _ ->
                AppCompatDelegate.setDefaultNightMode(themeMode)
                // TODO: Save night mode to shared preferences (callback?)
            }
            .setNeutralButton(R.string.button_cancel, null)
            .setSingleChoiceItems(themeTitles, checkedItem) { _, which ->
                themeMode = themeModes[which]
            }
            .show()
}
