package com.ricknout.rugbyranker.common.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ricknout.rugbyranker.common.livedata.Event

open class ReselectViewModel : ViewModel() {

    private val _navigateReselect = MutableLiveData<Event<Any>>()
    val navigateReselect: LiveData<Event<Any>>
        get() = _navigateReselect

    fun reselect() {
        _navigateReselect.value = Event(Any())
    }
}
