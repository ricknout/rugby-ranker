package dev.ricknout.rugbyranker.info.ui

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow

class InfoViewModel @ViewModelInject constructor() : ViewModel() {

    private val _version = MutableStateFlow<String?>(null)
    val version: LiveData<String?> = _version.asLiveData()

    fun setVersion(version: String) {
        _version.value = version
    }
}
