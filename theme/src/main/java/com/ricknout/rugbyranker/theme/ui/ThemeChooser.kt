package com.ricknout.rugbyranker.theme.ui

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ricknout.rugbyranker.theme.R
import com.ricknout.rugbyranker.theme.repository.ThemeRepository

class ThemeChooser(private val themeRepository: ThemeRepository) {

    fun showThemeChooser(context: Context) {
        val themes = themeRepository.getThemes()
        val themeModes = themes.map { theme -> theme.mode }
        val themeTitles = themes.map { theme -> context.getString(theme.title) }
        var themeMode = themeRepository.getThemeMode()
        val checkedItem = themeModes.indexOf(themeMode)
        MaterialAlertDialogBuilder(context)
                .setTitle(R.string.title_choose_theme)
                .setPositiveButton(R.string.button_ok) { _, _ ->
                    themeRepository.setThemeMode(themeMode)
                }
                .setNeutralButton(R.string.button_cancel, null)
                .setSingleChoiceItems(themeTitles.toTypedArray(), checkedItem) { _, which ->
                    themeMode = themeModes[which]
                }
                .show()
    }

    fun setDefaultTheme() {
        themeRepository.setDefaultThemeMode()
    }
}
