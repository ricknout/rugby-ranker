package com.ricknout.rugbyranker.teams.repository

import android.util.Log
import com.ricknout.rugbyranker.core.api.WorldRugbyService
import com.ricknout.rugbyranker.core.util.DateUtils
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.teams.db.WorldRugbyTeamDao
import com.ricknout.rugbyranker.teams.vo.TeamsDataConverter

class TeamsRepository(
    private val worldRugbyService: WorldRugbyService,
    private val worldRugbyTeamDao: WorldRugbyTeamDao
) {

    fun loadLatestWorldRugbyTeams(sport: Sport) = worldRugbyTeamDao.load(sport)

    suspend fun fetchAndCacheLatestWorldRugbyTeamsSync(sport: Sport): Boolean {
        val sports = when (sport) {
            Sport.MENS -> WorldRugbyService.SPORT_MENS
            Sport.WOMENS -> WorldRugbyService.SPORT_WOMENS
        }
        val date = getCurrentDate()
        return try {
            val worldRugbyRankingsResponse = worldRugbyService.getRankings(sports, date)
            val worldRugbyTeams = TeamsDataConverter.getWorldRugbyTeamsFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse, sport)
            worldRugbyTeamDao.insert(worldRugbyTeams)
            true
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            false
        }
    }

    private fun getCurrentDate() = DateUtils.getCurrentDate(DateUtils.DATE_FORMAT_YYYY_MM_DD)

    companion object {
        private const val TAG = "TeamsRepository"
    }
}
