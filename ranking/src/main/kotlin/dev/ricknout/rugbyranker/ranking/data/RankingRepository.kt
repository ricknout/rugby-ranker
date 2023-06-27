package dev.ricknout.rugbyranker.ranking.data

import android.util.Log
import dev.ricknout.rugbyranker.core.api.WorldRugbyService
import dev.ricknout.rugbyranker.core.db.RankingDao
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.core.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RankingRepository(
    private val service: WorldRugbyService,
    private val dao: RankingDao,
    private val dataStore: RankingDataStore,
) {

    fun loadRankings(sport: Sport) = dao.loadByPosition(sport)

    suspend fun fetchAndCacheLatestRankingsSync(sport: Sport): Boolean {
        val sports = when (sport) {
            Sport.MENS -> WorldRugbyService.SPORT_MENS
            Sport.WOMENS -> WorldRugbyService.SPORT_WOMENS
        }
        val date = DateUtils.getCurrentDate(DateUtils.DATE_FORMAT_YYYY_MM_DD)
        return try {
            val response = service.getRankings(sports, date)
            val rankings = RankingDataConverter.getRankingsFromResponse(response, sport)
            dao.insert(rankings)
            dataStore.setUpdatedTimeMillis(response.effective.millis, sport)
            true
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            false
        }
    }

    fun fetchAndCacheLatestRankingsAsync(
        sport: Sport,
        coroutineScope: CoroutineScope,
        onComplete: (success: Boolean) -> Unit,
    ) {
        coroutineScope.launch {
            val success = withContext(Dispatchers.IO) { fetchAndCacheLatestRankingsSync(sport) }
            onComplete(success)
        }
    }

    fun getUpdatedTimeMillis(sport: Sport) = dataStore.getUpdatedTimeMillis(sport)

    companion object {
        private const val TAG = "RankingRepository"
    }
}
