package dev.ricknout.rugbyranker.theme.data

import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.map

class ThemeDataStore(private val dataStore: DataStore<Preferences>) {

    suspend fun setMode(mode: Int) = dataStore.edit { preferences ->
        preferences[KEY_MODE] = mode
    }

    fun getMode(defaultMode: Int) = dataStore.data.map { preferences ->
        preferences[KEY_MODE] ?: defaultMode
    }

    companion object {
        private val KEY_MODE = preferencesKey<Int>("mode")
    }
}
