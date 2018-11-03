package com.ricknout.rugbyranker.prefs

import android.content.SharedPreferences
import com.ricknout.rugbyranker.vo.Sport
import androidx.core.content.edit
import androidx.lifecycle.LiveData

class RugbyRankerSharedPreferences(private val sharedPreferences: SharedPreferences) {

    fun setLatestWorldRugbyRankingsEffectiveTime(effectiveTime: String, sport: Sport) {
        when (sport) {
            Sport.MENS -> setMensEffectiveTime(effectiveTime)
            Sport.WOMENS -> setWomensEffectiveTime(effectiveTime)
        }
    }

    private fun setMensEffectiveTime(effectiveTime: String) = sharedPreferences.edit { putString(KEY_EFFECTIVE_TIME_MENS, effectiveTime) }

    private fun setWomensEffectiveTime(effectiveTime: String) = sharedPreferences.edit { putString(KEY_EFFECTIVE_TIME_WOMENS, effectiveTime) }

    fun getLatestWorldRugbyRankingsEffectiveTime(sport: Sport): String? = when (sport) {
        Sport.MENS -> getMensEffectiveTime()
        Sport.WOMENS -> getWomensEffectiveTime()
    }

    private fun getMensEffectiveTime() = sharedPreferences.getString(KEY_EFFECTIVE_TIME_MENS, null)

    private fun getWomensEffectiveTime() = sharedPreferences.getString(KEY_EFFECTIVE_TIME_WOMENS, null)

    fun getLatestWorldRugbyRankingsEffectiveTimeLiveData(sport: Sport): LiveData<String> = when (sport) {
        Sport.MENS -> getMensEffectiveTimeLiveData()
        Sport.WOMENS -> getWomensEffectiveTimeLiveData()
    }

    private fun getMensEffectiveTimeLiveData() = StringSharedPreferenceLiveData(sharedPreferences, KEY_EFFECTIVE_TIME_MENS, null)

    private fun getWomensEffectiveTimeLiveData() = StringSharedPreferenceLiveData(sharedPreferences, KEY_EFFECTIVE_TIME_WOMENS, null)

    companion object {
        const val SHARED_PREFERENCES_NAME = "rugby_ranker_shared_preferences"
        private const val KEY_EFFECTIVE_TIME_MENS = "effective_time_mens"
        private const val KEY_EFFECTIVE_TIME_WOMENS = "effective_time_womens"
    }
}
