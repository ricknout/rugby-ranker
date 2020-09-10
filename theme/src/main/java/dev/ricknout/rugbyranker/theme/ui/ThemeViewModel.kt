package dev.ricknout.rugbyranker.theme.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.ricknout.rugbyranker.theme.data.ThemeRepository
import dev.ricknout.rugbyranker.theme.model.Theme

class ThemeViewModel @ViewModelInject constructor(private val repository: ThemeRepository) : ViewModel() {

    val theme = repository.getTheme()

    fun getThemes() = repository.getThemes()

    fun setTheme(theme: Theme) = repository.setTheme(theme, viewModelScope)
}
