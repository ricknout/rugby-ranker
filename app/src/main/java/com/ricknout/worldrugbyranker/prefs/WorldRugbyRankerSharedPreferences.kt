package com.ricknout.worldrugbyranker.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import com.ricknout.worldrugbyranker.vo.RankingsType

class WorldRugbyRankerSharedPreferences(private val sharedPreferences: SharedPreferences) {

    fun setRefreshTime(rankingsType: RankingsType, millis: Long) {
        when (rankingsType) {
            RankingsType.MENS -> setMensRefreshTime(millis)
            RankingsType.WOMENS -> setWomensRefreshTime(millis)
        }
    }

    private fun setMensRefreshTime(millis: Long) = sharedPreferences.edit { putLong(KEY_REFRESH_TIME_MENS, millis) }

    private fun setWomensRefreshTime(millis: Long) = sharedPreferences.edit { putLong(KEY_REFRESH_TIME_WOMENS, millis) }

    fun getRefreshTime(rankingsType: RankingsType) = when (rankingsType) {
        RankingsType.MENS -> getMensRefreshTime()
        RankingsType.WOMENS -> getWomensRefreshTime()
    }

    private fun getMensRefreshTime() = sharedPreferences.getLong(KEY_REFRESH_TIME_MENS, DEFAULT_REFRESH_TIME)

    private fun getWomensRefreshTime() = sharedPreferences.getLong(KEY_REFRESH_TIME_WOMENS, DEFAULT_REFRESH_TIME)

    companion object {
        private const val KEY_REFRESH_TIME_MENS = "refresh_time_mens"
        private const val KEY_REFRESH_TIME_WOMENS = "refresh_time_womens"
        private const val DEFAULT_REFRESH_TIME = 0L
    }
}
