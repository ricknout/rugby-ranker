package com.ricknout.rugbyranker.matches.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.matches.vo.MatchStatus

class MatchesSharedPreferences(private val sharedPreferences: SharedPreferences) {

    fun setInitialMatchesFetched(sport: Sport, matchStatus: MatchStatus, fetched: Boolean) = sharedPreferences.edit {
        putBoolean(getInitialMatchesFetchedKey(sport, matchStatus), fetched)
    }

    fun isInitialMatchesFetched(sport: Sport, matchStatus: MatchStatus) = sharedPreferences.getBoolean(
        getInitialMatchesFetchedKey(sport, matchStatus), false
    )

    companion object {
        private const val KEY_INITIAL_MATCHES_FETCHED = "initial_matches_fetched"
        private fun getInitialMatchesFetchedKey(sport: Sport, matchStatus: MatchStatus): String {
            return "${KEY_INITIAL_MATCHES_FETCHED}_${sport}_$matchStatus"
        }
    }
}
