package com.ricknout.rugbyranker.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.ricknout.rugbyranker.api.WorldRugbyService
import com.ricknout.rugbyranker.common.util.DateUtils
import com.ricknout.rugbyranker.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.prefs.RugbyRankerSharedPreferences
import com.ricknout.rugbyranker.vo.RankingsType
import com.ricknout.rugbyranker.vo.WorldRugbyRankingDataConverter
import java.util.concurrent.Executor

class RugbyRankerRepository(
        private val worldRugbyService: WorldRugbyService,
        private val worldRugbyRankingDao: WorldRugbyRankingDao,
        private val rugbyRankerSharedPreferences: RugbyRankerSharedPreferences,
        private val executor: Executor
) {

    fun loadLatestWorldRugbyRankings(rankingsType: RankingsType) = worldRugbyRankingDao.load(rankingsType)

    @WorkerThread
    fun fetchAndCacheLatestWorldRugbyRankings(rankingsType: RankingsType): Boolean {
        val json = when (rankingsType) {
            RankingsType.MENS -> WorldRugbyService.JSON_MENS
            RankingsType.WOMENS -> WorldRugbyService.JSON_WOMENS
        }
        val date = getCurrentDate()
        val response = worldRugbyService.getRankings(json, date).execute()
        if (response.isSuccessful) {
            val worldRugbyRankingsResponse = response.body() ?: return false
            val worldRugbyRankings = WorldRugbyRankingDataConverter.getWorldRugbyRankingsFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse, rankingsType)
            worldRugbyRankingDao.insert(worldRugbyRankings)
            val effectiveTime = WorldRugbyRankingDataConverter.getEffectiveTimeFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse)
            rugbyRankerSharedPreferences.setLatestWorldRugbyRankingsEffectiveTime(effectiveTime, rankingsType)
            return true
        }
        return false
    }

    private fun getCurrentDate() = DateUtils.getCurrentDate(DateUtils.DATE_FORMAT)

    fun getLatestWorldRugbyRankingsEffectiveTime(rankingsType: RankingsType): String? {
        return rugbyRankerSharedPreferences.getLatestWorldRugbyRankingsEffectiveTime(rankingsType)
    }

    fun getLatestWorldRugbyRankingsEffectiveTimeLiveData(rankingsType: RankingsType): LiveData<String> {
        return rugbyRankerSharedPreferences.getLatestWorldRugbyRankingsEffectiveTimeLiveData(rankingsType)
    }
}
