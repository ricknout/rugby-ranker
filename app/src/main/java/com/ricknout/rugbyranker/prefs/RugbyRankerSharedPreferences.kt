package com.ricknout.rugbyranker.prefs

import android.content.SharedPreferences
import com.ricknout.rugbyranker.vo.RankingsType
import androidx.core.content.edit
import androidx.lifecycle.LiveData

class RugbyRankerSharedPreferences(private val sharedPreferences: SharedPreferences) {

    fun setLatestWorldRugbyRankingsEffectiveTimeMillis(millis: Long, rankingsType: RankingsType) {
        when (rankingsType) {
            RankingsType.MENS -> setMensEffectiveTimeMillis(millis)
            RankingsType.WOMENS -> setWomensEffectiveTimeMillis(millis)
        }
    }

    private fun setMensEffectiveTimeMillis(millis: Long) = sharedPreferences.edit { putLong(KEY_EFFECTIVE_TIME_MILLIS_MENS, millis) }

    private fun setWomensEffectiveTimeMillis(millis: Long) = sharedPreferences.edit { putLong(KEY_EFFECTIVE_TIME_MILLIS_WOMENS, millis) }

    fun getLatestWorldRugbyRankingsEffectiveTimeMillis(rankingsType: RankingsType) = when (rankingsType) {
        RankingsType.MENS -> getMensEffectiveTimeMillis()
        RankingsType.WOMENS -> getWomensEffectiveTimeMillis()
    }

    private fun getMensEffectiveTimeMillis() = sharedPreferences.getLong(KEY_EFFECTIVE_TIME_MILLIS_MENS, DEFAULT_EFFECTIVE_TIME_MILLIS)

    private fun getWomensEffectiveTimeMillis() = sharedPreferences.getLong(KEY_EFFECTIVE_TIME_MILLIS_WOMENS, DEFAULT_EFFECTIVE_TIME_MILLIS)

    fun getLatestWorldRugbyRankingsEffectiveTimeMillisLiveData(rankingsType: RankingsType): LiveData<Long> = when (rankingsType) {
        RankingsType.MENS -> getMensEffectiveTimeMillisLiveData()
        RankingsType.WOMENS -> getWomensEffectiveTimeMillisLiveData()
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
