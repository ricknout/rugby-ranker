package com.ricknout.rugbyranker.theme.ui

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ricknout.rugbyranker.theme.R

fun showThemeChooser(context: Context) {
    var nightMode = AppCompatDelegate.getDefaultNightMode()
    // TODO: Consider other night mode types
    val checkedItem = when (nightMode) {
        AppCompatDelegate.MODE_NIGHT_NO -> 0
        AppCompatDelegate.MODE_NIGHT_YES -> 1
        else -> 2
    }
    MaterialAlertDialogBuilder(context)
            .setTitle(R.string.title_choose_theme)
            .setPositiveButton(R.string.button_ok) { _, _ ->
                AppCompatDelegate.setDefaultNightMode(nightMode)
                // TODO: Save night mode to shared preferences (callback?)
            }
            .setNeutralButton(R.string.button_cancel, null)
            .setSingleChoiceItems(R.array.themes, checkedItem) { _, which ->
                // TODO: Consider other night mode types
                nightMode = when (which) {
                    0 -> AppCompatDelegate.MODE_NIGHT_NO
                    1 -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            }
            .show()
}
