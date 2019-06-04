package com.ricknout.rugbyranker.theme.ui

import androidx.lifecycle.ViewModel
import com.ricknout.rugbyranker.theme.repository.ThemeRepository
import com.ricknout.rugbyranker.theme.vo.Theme
import javax.inject.Inject

class ThemeViewModel @Inject constructor(
        private val themeRepository: ThemeRepository
) : ViewModel() {

    fun getTheme() = themeRepository.getTheme()

    fun getThemes() = themeRepository.getThemes()

    fun setTheme(theme: Theme) = themeRepository.setTheme(theme)
}
