package dev.ricknout.rugbyranker.info.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class InfoViewModel
    @Inject
    constructor() : ViewModel() {
        private val _version = MutableStateFlow<String?>(null)
        val version: StateFlow<String?> = _version

        fun setVersion(version: String) {
            _version.value = version
        }
    }
