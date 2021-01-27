package dev.ricknout.rugbyranker.info.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor() : ViewModel() {

    private val _version = MutableStateFlow<String?>(null)
    val version: LiveData<String?> = _version.asLiveData()

    fun setVersion(version: String) {
        _version.value = version
    }
}
