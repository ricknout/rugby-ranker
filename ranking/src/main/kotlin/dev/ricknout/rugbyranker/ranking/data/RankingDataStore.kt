package dev.ricknout.rugbyranker.ranking.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import dev.ricknout.rugbyranker.core.model.Sport
import kotlinx.coroutines.flow.map

class RankingDataStore(private val dataStore: DataStore<Preferences>) {
    suspend fun setUpdatedTimeMillis(
        millis: Long,
        sport: Sport,
    ) = dataStore.edit { preferences ->
        preferences[getUpdatedTimeMillisKey(sport)] = millis
    }

    fun getUpdatedTimeMillis(sport: Sport) =
        dataStore.data.map { preferences ->
            preferences[getUpdatedTimeMillisKey(sport)] ?: DEFAULT_UPDATED_TIME_MILLIS
        }

    companion object {
        const val DEFAULT_UPDATED_TIME_MILLIS = -1L
        private const val KEY_UPDATED_TIME_MILLIS = "updated_time_millis"

        private fun getUpdatedTimeMillisKey(sport: Sport) =
            longPreferencesKey(
                "${KEY_UPDATED_TIME_MILLIS}_$sport",
            )
    }
}
