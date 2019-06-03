package com.ricknout.rugbyranker.theme.ui

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ricknout.rugbyranker.theme.R
import com.ricknout.rugbyranker.theme.repository.ThemeRepository

class ThemeChooser(private val themeRepository: ThemeRepository) {

    fun showThemeChooser(context: Context) {
        val themes = themeRepository.getThemes()
        val themeTitles = themes.map { theme -> context.getString(theme.title) }.toTypedArray()
        var theme = themeRepository.getTheme()
        val checkedItem = themes.indexOf(theme)
        MaterialAlertDialogBuilder(context)
                .setTitle(R.string.title_choose_theme)
                .setPositiveButton(R.string.button_ok) { _, _ ->
                    themeRepository.setTheme(theme)
                }
                .setNeutralButton(R.string.button_cancel, null)
                .setSingleChoiceItems(themeTitles, checkedItem) { _, which ->
                    theme = themes[which]
                }
                .show()
    }

    fun setDefaultTheme() = themeRepository.setDefaultTheme()
}
