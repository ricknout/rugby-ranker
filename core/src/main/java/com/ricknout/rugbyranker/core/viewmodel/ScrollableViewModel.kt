package com.ricknout.rugbyranker.core.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ricknout.rugbyranker.core.livedata.Event

open class ScrollableViewModel : ViewModel() {

    private val _onScroll = MutableLiveData<Event<Int>>()
    val onScroll: LiveData<Event<Int>>
        get() = _onScroll

    private val _scrollToTop = MutableLiveData<Event<Any>>()
    val scrollToTop: LiveData<Event<Any>>
        get() = _scrollToTop

    fun onScroll(delta: Int) {
        _onScroll.value = Event(delta)
    }

    fun scrollToTop() {
        _scrollToTop.value = Event(Any())
    }
}
