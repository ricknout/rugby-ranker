package com.ricknout.rugbyranker.common.prefs

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

abstract class SharedPreferenceLiveData<T>(
    val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defValue: T
) : LiveData<T>() {

    private val onSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == this.key) {
            value = getValueFromSharedPreferences(key, defValue)
        }
    }

    abstract fun getValueFromSharedPreferences(key: String, defValue: T): T

    override fun onActive() {
        super.onActive()
        value = getValueFromSharedPreferences(key, defValue)
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    override fun onInactive() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
        super.onInactive()
    }
}

class LongSharedPreferenceLiveData(
    sharedPreferences: SharedPreferences,
    key: String,
    defValue: Long
) : SharedPreferenceLiveData<Long>(sharedPreferences, key, defValue) {

    override fun getValueFromSharedPreferences(key: String, defValue: Long): Long = sharedPreferences.getLong(key, defValue)
}
