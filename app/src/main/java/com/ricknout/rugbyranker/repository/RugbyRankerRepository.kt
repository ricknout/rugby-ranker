package com.ricknout.rugbyranker.repository

import com.ricknout.rugbyranker.api.WorldRugbyService
import com.ricknout.rugbyranker.common.util.DateUtils
import com.ricknout.rugbyranker.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.vo.RankingsType
import com.ricknout.rugbyranker.vo.WorldRugbyRankingDataConverter

class RugbyRankerRepository(
        private val worldRugbyService: WorldRugbyService,
        private val worldRugbyRankingDao: WorldRugbyRankingDao
) {

    fun loadLatestWorldRugbyRankings(rankingsType: RankingsType) = worldRugbyRankingDao.load(rankingsType)

    fun fetchAndCacheLatestWorldRugbyRankings(rankingsType: RankingsType): Boolean {
        val json = when (rankingsType) {
            RankingsType.MENS -> WorldRugbyService.JSON_MENS
            RankingsType.WOMENS -> WorldRugbyService.JSON_WOMENS
        }
        val date = getCurrentDate()
        val response = worldRugbyService.getRankings(json, date).execute()
        if (response.isSuccessful) {
            val worldRugbyRankingsResponse = response.body() ?: return false
            val worldRugbyRankings = WorldRugbyRankingDataConverter.convertFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse, rankingsType)
            worldRugbyRankingDao.insert(worldRugbyRankings)
            return true
        }
        return false
    }

    private fun getCurrentDate() = DateUtils.getCurrentDate(WorldRugbyService.DATE_FORMAT)
}
