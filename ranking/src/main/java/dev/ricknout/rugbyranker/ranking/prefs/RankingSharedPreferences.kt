package dev.ricknout.rugbyranker.ranking.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.core.prefs.getLongAsFlow
import kotlinx.coroutines.flow.Flow

class RankingSharedPreferences(private val sharedPreferences: SharedPreferences) {

    fun setUpdatedTimeMillis(millis: Long, sport: Sport) = sharedPreferences.edit {
        putLong(getUpdatedTimeMillisKey(sport), millis)
    }

    fun getUpdatedTimeMillis(sport: Sport) = sharedPreferences.getLong(
        getUpdatedTimeMillisKey(sport),
        DEFAULT_UPDATED_TIME_MILLIS
    )

    fun getUpdatedTimeMillisFlow(sport: Sport): Flow<Long> = sharedPreferences.getLongAsFlow(
        getUpdatedTimeMillisKey(sport),
        DEFAULT_UPDATED_TIME_MILLIS
    )

    companion object {
        const val DEFAULT_UPDATED_TIME_MILLIS = -1L
        private const val KEY_UPDATED_TIME_MILLIS = "updated_time_millis"
        private fun getUpdatedTimeMillisKey(sport: Sport): String {
            return "${KEY_UPDATED_TIME_MILLIS}_$sport"
        }
    }
}
