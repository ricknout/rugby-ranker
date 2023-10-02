package dev.ricknout.rugbyranker.theme.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ricknout.rugbyranker.theme.data.ThemeRepository
import dev.ricknout.rugbyranker.theme.model.Theme
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel
    @Inject
    constructor(private val repository: ThemeRepository) : ViewModel() {
        val theme = repository.getTheme()

        fun getThemes() = repository.getThemes()

        fun setTheme(theme: Theme) = repository.setTheme(theme, viewModelScope)
    }
