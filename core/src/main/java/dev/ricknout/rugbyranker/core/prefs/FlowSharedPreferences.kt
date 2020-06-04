package dev.ricknout.rugbyranker.core.prefs

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

private inline fun <reified T> SharedPreferences.getValueAsFlow(
    key: String,
    defValue: T,
    crossinline getValue: (key: String, defValue: T) -> T
): Flow<T> = callbackFlow {
    offer(getValue(key, defValue))
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, k ->
        if (key == k) offer(getValue(key, defValue))
    }
    registerOnSharedPreferenceChangeListener(listener)
    awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
}

fun SharedPreferences.getStringAsFlow(key: String, defValue: String?): Flow<String?> =
    getValueAsFlow(key, defValue) { k, dv -> getString(k, dv) }

fun SharedPreferences.getStringSetAsFlow(key: String, defValue: Set<String>?): Flow<Set<String>?> =
    getValueAsFlow(key, defValue) { k, dv -> getStringSet(k, dv) }

fun SharedPreferences.getIntAsFlow(key: String, defValue: Int): Flow<Int> =
    getValueAsFlow(key, defValue) { k, dv -> getInt(k, dv) }

fun SharedPreferences.getLongAsFlow(key: String, defValue: Long): Flow<Long> =
    getValueAsFlow(key, defValue) { k, dv -> getLong(k, dv) }

fun SharedPreferences.getFloatAsFlow(key: String, defValue: Float): Flow<Float> =
    getValueAsFlow(key, defValue) { k, dv -> getFloat(k, dv) }

fun SharedPreferences.getBooleanAsFlow(key: String, defValue: Boolean): Flow<Boolean> =
    getValueAsFlow(key, defValue) { k, dv -> getBoolean(k, dv) }
