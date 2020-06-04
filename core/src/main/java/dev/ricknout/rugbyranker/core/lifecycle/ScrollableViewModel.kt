package dev.ricknout.rugbyranker.core.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow

open class ScrollableViewModel : ViewModel() {

    private val _scrollToTop = MutableStateFlow(false)
    val scrollToTop: LiveData<Boolean> = _scrollToTop.asLiveData()

    fun scrollToTop() {
        _scrollToTop.value = true
    }

    fun resetScrollToTop() {
        _scrollToTop.value = false
    }
}
