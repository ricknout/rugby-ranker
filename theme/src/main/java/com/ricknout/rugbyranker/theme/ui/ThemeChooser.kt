package com.ricknout.rugbyranker.theme.ui

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ricknout.rugbyranker.theme.R
import com.ricknout.rugbyranker.theme.repository.ThemeRepository

class ThemeChooser(private val themeRepository: ThemeRepository) {

    fun showThemeChooser(context: Context) {
        val themes = themeRepository.getThemes()
        val themeTitles = themes.map { theme -> context.getString(theme.titleResId) }.toTypedArray()
        val theme = themeRepository.getTheme()
        val checkedItem = themes.indexOf(theme)
        MaterialAlertDialogBuilder(context)
                .setTitle(R.string.title_choose_theme)
                .setSingleChoiceItems(themeTitles, checkedItem) { dialog, which ->
                    val whichTheme = themes[which]
                    themeRepository.setTheme(whichTheme)
                    dialog.dismiss()
                }
                .show()
    }

    fun setDefaultTheme() = themeRepository.setDefaultTheme()
}
