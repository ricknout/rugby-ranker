package dev.ricknout.rugbyranker.theme.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dev.ricknout.rugbyranker.theme.data.ThemeRepository
import dev.ricknout.rugbyranker.theme.model.Theme

class ThemeViewModel @ViewModelInject constructor(private val repository: ThemeRepository) : ViewModel() {

    fun getTheme() = repository.getTheme()

    fun getThemes() = repository.getThemes()

    fun setTheme(theme: Theme) = repository.setTheme(theme)
}
