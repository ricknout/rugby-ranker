package com.ricknout.rugbyranker.rankings.repository

import androidx.lifecycle.LiveData
import com.ricknout.rugbyranker.core.api.WorldRugbyService
import com.ricknout.rugbyranker.core.util.DateUtils
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.rankings.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.rankings.prefs.RankingsSharedPreferences
import com.ricknout.rugbyranker.rankings.vo.RankingsDataConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RankingsRepository(
    private val worldRugbyService: WorldRugbyService,
    private val worldRugbyRankingDao: WorldRugbyRankingDao,
    private val rankingsSharedPreferences: RankingsSharedPreferences
) {

    fun loadLatestWorldRugbyRankings(sport: Sport) = worldRugbyRankingDao.load(sport)

    fun loadLatestWorldRugbyRankingsTeamIds(sport: Sport) = worldRugbyRankingDao.loadTeamIds(sport)

    suspend fun fetchAndCacheLatestWorldRugbyRankingsSync(sport: Sport): Boolean {
        val json = when (sport) {
            Sport.MENS -> WorldRugbyService.JSON_MENS
            Sport.WOMENS -> WorldRugbyService.JSON_WOMENS
        }
        val date = getCurrentDate()
        return try {
            val worldRugbyRankingsResponse = worldRugbyService.getRankingsAsync(json, date).await()
            val worldRugbyRankings = RankingsDataConverter.getWorldRugbyRankingsFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse, sport)
            worldRugbyRankingDao.insert(worldRugbyRankings)
            rankingsSharedPreferences.setLatestWorldRugbyRankingsEffectiveTimeMillis(worldRugbyRankingsResponse.effective.millis, sport)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun fetchAndCacheLatestWorldRugbyRankingsAsync(sport: Sport, coroutineScope: CoroutineScope, onComplete: (success: Boolean) -> Unit) {
        coroutineScope.launch {
            val success = withContext(Dispatchers.IO) { fetchAndCacheLatestWorldRugbyRankingsSync(sport) }
            onComplete(success)
        }
    }

    private fun getCurrentDate() = DateUtils.getCurrentDate(DateUtils.DATE_FORMAT_YYYY_MM_DD)

    fun getLatestWorldRugbyRankingsEffectiveTimeMillis(sport: Sport): Long {
        return rankingsSharedPreferences.getLatestWorldRugbyRankingsEffectiveTimeMillis(sport)
    }

    fun getLatestWorldRugbyRankingsEffectiveTimeMillisLiveData(sport: Sport): LiveData<Long> {
        return rankingsSharedPreferences.getLatestWorldRugbyRankingsEffectiveTimeMillisLiveData(sport)
    }
}
