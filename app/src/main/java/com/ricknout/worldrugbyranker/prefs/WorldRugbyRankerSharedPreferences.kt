package com.ricknout.worldrugbyranker.prefs

import android.content.SharedPreferences
import androidx.core.content.edit

class WorldRugbyRankerSharedPreferences(private val sharedPreferences: SharedPreferences) {

    fun setMensRefreshTime(millis: Long) = sharedPreferences.edit { putLong(KEY_REFRESH_TIME_MENS, millis) }

    fun setWomensRefreshTime(millis: Long) = sharedPreferences.edit { putLong(KEY_REFRESH_TIME_WOMENS, millis) }

    fun getMensRefreshTime() = sharedPreferences.getLong(KEY_REFRESH_TIME_MENS, DEFAULT_REFRESH_TIME)

    fun getWomensRefreshTime() = sharedPreferences.getLong(KEY_REFRESH_TIME_WOMENS, DEFAULT_REFRESH_TIME)

    companion object {
        private const val KEY_REFRESH_TIME_MENS = "refresh_time_mens"
        private const val KEY_REFRESH_TIME_WOMENS = "refresh_time_womens"
        private const val DEFAULT_REFRESH_TIME = 0L
    }
}