package com.ricknout.rugbyranker.prefs

import android.content.SharedPreferences
import com.ricknout.rugbyranker.common.vo.Sport
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import com.ricknout.rugbyranker.common.prefs.LongSharedPreferenceLiveData

class RugbyRankerSharedPreferences(private val sharedPreferences: SharedPreferences) {

    fun setLatestWorldRugbyRankingsEffectiveTimeMillis(millis: Long, sport: Sport) {
        when (sport) {
            Sport.MENS -> setMensEffectiveTimeMillis(millis)
            Sport.WOMENS -> setWomensEffectiveTimeMillis(millis)
        }
    }

    private fun setMensEffectiveTimeMillis(millis: Long) = sharedPreferences.edit { putLong(KEY_EFFECTIVE_TIME_MILLIS_MENS, millis) }

    private fun setWomensEffectiveTimeMillis(millis: Long) = sharedPreferences.edit { putLong(KEY_EFFECTIVE_TIME_MILLIS_WOMENS, millis) }

    fun getLatestWorldRugbyRankingsEffectiveTimeMillis(sport: Sport) = when (sport) {
        Sport.MENS -> getMensEffectiveTimeMillis()
        Sport.WOMENS -> getWomensEffectiveTimeMillis()
    }

    private fun getMensEffectiveTimeMillis() = sharedPreferences.getLong(KEY_EFFECTIVE_TIME_MILLIS_MENS, DEFAULT_EFFECTIVE_TIME_MILLIS)

    private fun getWomensEffectiveTimeMillis() = sharedPreferences.getLong(KEY_EFFECTIVE_TIME_MILLIS_WOMENS, DEFAULT_EFFECTIVE_TIME_MILLIS)

    fun getLatestWorldRugbyRankingsEffectiveTimeMillisLiveData(sport: Sport): LiveData<Long> = when (sport) {
        Sport.MENS -> getMensEffectiveTimeMillisLiveData()
        Sport.WOMENS -> getWomensEffectiveTimeMillisLiveData()
    }

    private fun getMensEffectiveTimeMillisLiveData() = LongSharedPreferenceLiveData(sharedPreferences, KEY_EFFECTIVE_TIME_MILLIS_MENS, DEFAULT_EFFECTIVE_TIME_MILLIS)

    private fun getWomensEffectiveTimeMillisLiveData() = LongSharedPreferenceLiveData(sharedPreferences, KEY_EFFECTIVE_TIME_MILLIS_WOMENS, DEFAULT_EFFECTIVE_TIME_MILLIS)

    companion object {
        const val SHARED_PREFERENCES_NAME = "rugby_ranker_shared_preferences"
        const val DEFAULT_EFFECTIVE_TIME_MILLIS = -1L
        private const val KEY_EFFECTIVE_TIME_MILLIS_MENS = "effective_time_millis_mens"
        private const val KEY_EFFECTIVE_TIME_MILLIS_WOMENS = "effective_time_millis_womens"
    }
}
