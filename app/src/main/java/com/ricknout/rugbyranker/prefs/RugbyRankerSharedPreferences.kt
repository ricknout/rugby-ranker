package com.ricknout.rugbyranker.prefs

import android.content.SharedPreferences
import com.ricknout.rugbyranker.vo.RankingsType
import androidx.core.content.edit
import androidx.lifecycle.LiveData

class RugbyRankerSharedPreferences(private val sharedPreferences: SharedPreferences) {

    fun setLatestWorldRugbyRankingsEffectiveTime(effectiveTime: String, rankingsType: RankingsType) {
        when (rankingsType) {
            RankingsType.MENS -> setMensEffectiveTime(effectiveTime)
            RankingsType.WOMENS -> setWomensEffectiveTime(effectiveTime)
        }
    }

    private fun setMensEffectiveTime(effectiveTime: String) = sharedPreferences.edit { putString(KEY_EFFECTIVE_TIME_MENS, effectiveTime) }

    private fun setWomensEffectiveTime(effectiveTime: String) = sharedPreferences.edit { putString(KEY_EFFECTIVE_TIME_WOMENS, effectiveTime) }

    fun getLatestWorldRugbyRankingsEffectiveTimeLiveData(rankingsType: RankingsType): LiveData<String> = when (rankingsType) {
        RankingsType.MENS -> getMensEffectiveTimeLiveData()
        RankingsType.WOMENS -> getWomensEffectiveTimeLiveData()
    }

    private fun getMensEffectiveTimeLiveData() = StringSharedPreferenceLiveData(sharedPreferences, KEY_EFFECTIVE_TIME_MENS, null)

    private fun getWomensEffectiveTimeLiveData() = StringSharedPreferenceLiveData(sharedPreferences, KEY_EFFECTIVE_TIME_WOMENS, null)

    companion object {
        const val SHARED_PREFERENCES_NAME = "rugby_ranker_shared_preferences"
        private const val KEY_EFFECTIVE_TIME_MENS = "effective_time_mens"
        private const val KEY_EFFECTIVE_TIME_WOMENS = "effective_time_womens"
    }
}
