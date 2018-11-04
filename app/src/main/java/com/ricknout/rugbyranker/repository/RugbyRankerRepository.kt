package com.ricknout.rugbyranker.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.ricknout.rugbyranker.api.WorldRugbyService
import com.ricknout.rugbyranker.common.util.DateUtils
import com.ricknout.rugbyranker.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.prefs.RugbyRankerSharedPreferences
import com.ricknout.rugbyranker.vo.Sport
import com.ricknout.rugbyranker.vo.WorldRugbyDataConverter

class RugbyRankerRepository(
        private val worldRugbyService: WorldRugbyService,
        private val worldRugbyRankingDao: WorldRugbyRankingDao,
        private val rugbyRankerSharedPreferences: RugbyRankerSharedPreferences
) {

    fun loadLatestWorldRugbyRankings(sport: Sport) = worldRugbyRankingDao.load(sport)

    @WorkerThread
    fun fetchAndCacheLatestWorldRugbyRankings(sport: Sport): Boolean {
        val json = when (sport) {
            Sport.MENS -> WorldRugbyService.JSON_MENS
            Sport.WOMENS -> WorldRugbyService.JSON_WOMENS
        }
        val date = getCurrentDate()
        val response = worldRugbyService.getRankings(json, date).execute()
        if (response.isSuccessful) {
            val worldRugbyRankingsResponse = response.body() ?: return false
            val worldRugbyRankings = WorldRugbyDataConverter.getWorldRugbyRankingsFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse, sport)
            worldRugbyRankingDao.insert(worldRugbyRankings)
            val effectiveTime = WorldRugbyDataConverter.getEffectiveTimeFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse)
            rugbyRankerSharedPreferences.setLatestWorldRugbyRankingsEffectiveTime(effectiveTime, sport)
            return true
        }
        return false
    }

    private fun getCurrentDate() = DateUtils.getCurrentDate(DateUtils.DATE_FORMAT)

    fun getLatestWorldRugbyRankingsEffectiveTime(sport: Sport): String? {
        return rugbyRankerSharedPreferences.getLatestWorldRugbyRankingsEffectiveTime(sport)
    }

    fun getLatestWorldRugbyRankingsEffectiveTimeLiveData(sport: Sport): LiveData<String> {
        return rugbyRankerSharedPreferences.getLatestWorldRugbyRankingsEffectiveTimeLiveData(sport)
    }
}
