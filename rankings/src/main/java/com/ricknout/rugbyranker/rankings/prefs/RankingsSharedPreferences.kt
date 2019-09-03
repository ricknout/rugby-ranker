package com.ricknout.rugbyranker.rankings.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import com.ricknout.rugbyranker.core.prefs.LongSharedPreferenceLiveData
import com.ricknout.rugbyranker.core.vo.Sport

class RankingsSharedPreferences(private val sharedPreferences: SharedPreferences) {

    fun setLatestWorldRugbyRankingsEffectiveTimeMillis(millis: Long, sport: Sport) = sharedPreferences.edit {
        putLong(getEffectiveTimeMillisKey(sport), millis)
    }

    fun getLatestWorldRugbyRankingsEffectiveTimeMillis(sport: Sport) = sharedPreferences.getLong(
        getEffectiveTimeMillisKey(sport), DEFAULT_EFFECTIVE_TIME_MILLIS
    )

    fun getLatestWorldRugbyRankingsEffectiveTimeMillisLiveData(sport: Sport): LiveData<Long> =
            LongSharedPreferenceLiveData(sharedPreferences, getEffectiveTimeMillisKey(sport), DEFAULT_EFFECTIVE_TIME_MILLIS)

    fun setInitialRankingsFetched(sport: Sport, fetched: Boolean) = sharedPreferences.edit {
        putBoolean(getInitialRankingsFetchedKey(sport), fetched)
    }

    fun isInitialRankingsFetched(sport: Sport) = sharedPreferences.getBoolean(
        getInitialRankingsFetchedKey(sport), false
    )

    companion object {
        const val DEFAULT_EFFECTIVE_TIME_MILLIS = -1L
        private const val KEY_EFFECTIVE_TIME_MILLIS = "effective_time_millis"
        private const val KEY_INITIAL_RANKINGS_FETCHED = "initial_rankings_fetched"
        private fun getEffectiveTimeMillisKey(sport: Sport): String {
            return "${KEY_EFFECTIVE_TIME_MILLIS}_$sport"
        }
        private fun getInitialRankingsFetchedKey(sport: Sport): String {
            return "${KEY_INITIAL_RANKINGS_FETCHED}_$sport"
        }
    }
}
