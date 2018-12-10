package com.ricknout.rugbyranker.rankings.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.ricknout.rugbyranker.common.api.WorldRugbyRankingsResponse
import com.ricknout.rugbyranker.common.api.WorldRugbyService
import com.ricknout.rugbyranker.common.util.DateUtils
import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.rankings.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.rankings.prefs.RankingsSharedPreferences
import com.ricknout.rugbyranker.rankings.vo.RankingsDataConverter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class RankingsRepository(
        private val worldRugbyService: WorldRugbyService,
        private val worldRugbyRankingDao: WorldRugbyRankingDao,
        private val rankingsSharedPreferences: RankingsSharedPreferences,
        private val executor: Executor
) {

    fun loadLatestWorldRugbyRankings(sport: Sport) = worldRugbyRankingDao.load(sport)

    @WorkerThread
    fun fetchAndCacheLatestWorldRugbyRankingsSync(sport: Sport): Boolean {
        val json = when (sport) {
            Sport.MENS -> WorldRugbyService.JSON_MENS
            Sport.WOMENS -> WorldRugbyService.JSON_WOMENS
        }
        val date = getCurrentDate()
        try {
            val response = worldRugbyService.getRankings(json, date).execute()
            if (response.isSuccessful) {
                val worldRugbyRankingsResponse = response.body() ?: return false
                val worldRugbyRankings = RankingsDataConverter.getWorldRugbyRankingsFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse, sport)
                executor.execute { worldRugbyRankingDao.insert(worldRugbyRankings) }
                rankingsSharedPreferences.setLatestWorldRugbyRankingsEffectiveTimeMillis(worldRugbyRankingsResponse.effective.millis, sport)
                return true
            }
            return false
        } catch (_: Exception) {
            return false
        }
    }

    fun fetchAndCacheLatestWorldRugbyRankingsAsync(sport: Sport, onComplete: (success: Boolean) -> Unit) {
        val json = when (sport) {
            Sport.MENS -> WorldRugbyService.JSON_MENS
            Sport.WOMENS -> WorldRugbyService.JSON_WOMENS
        }
        val date = getCurrentDate()
        val callback = object : Callback<WorldRugbyRankingsResponse> {

            override fun onResponse(call: Call<WorldRugbyRankingsResponse>, response: Response<WorldRugbyRankingsResponse>) {
                if (response.isSuccessful) {
                    val worldRugbyRankingsResponse = response.body()
                    if (worldRugbyRankingsResponse == null) {
                        onComplete(false)
                        return
                    }
                    val worldRugbyRankings = RankingsDataConverter.getWorldRugbyRankingsFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse, sport)
                    executor.execute { worldRugbyRankingDao.insert(worldRugbyRankings) }
                    rankingsSharedPreferences.setLatestWorldRugbyRankingsEffectiveTimeMillis(worldRugbyRankingsResponse.effective.millis, sport)
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }

            override fun onFailure(call: Call<WorldRugbyRankingsResponse>, t: Throwable) {
                onComplete(false)
            }
        }
        worldRugbyService.getRankings(json, date).enqueue(callback)
    }

    private fun getCurrentDate() = DateUtils.getCurrentDate(DateUtils.DATE_FORMAT_YYYY_MM_DD)

    fun getLatestWorldRugbyRankingsEffectiveTimeMillis(sport: Sport): Long {
        return rankingsSharedPreferences.getLatestWorldRugbyRankingsEffectiveTimeMillis(sport)
    }

    fun getLatestWorldRugbyRankingsEffectiveTimeMillisLiveData(sport: Sport): LiveData<Long> {
        return rankingsSharedPreferences.getLatestWorldRugbyRankingsEffectiveTimeMillisLiveData(sport)
    }
}
