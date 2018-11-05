package com.ricknout.rugbyranker.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.ricknout.rugbyranker.api.WorldRugbyRankingsResponse
import com.ricknout.rugbyranker.api.WorldRugbyService
import com.ricknout.rugbyranker.common.util.DateUtils
import com.ricknout.rugbyranker.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.prefs.RugbyRankerSharedPreferences
import com.ricknout.rugbyranker.vo.Sport
import com.ricknout.rugbyranker.vo.WorldRugbyDataConverter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class RugbyRankerRepository(
        private val worldRugbyService: WorldRugbyService,
        private val worldRugbyRankingDao: WorldRugbyRankingDao,
        private val rugbyRankerSharedPreferences: RugbyRankerSharedPreferences,
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
        val response = worldRugbyService.getRankings(json, date).execute()
        if (response.isSuccessful) {
            val worldRugbyRankingsResponse = response.body() ?: return false
            val worldRugbyRankings = WorldRugbyDataConverter.getWorldRugbyRankingsFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse, sport)
            executor.execute {
                worldRugbyRankingDao.insert(worldRugbyRankings)
            }
            val effectiveTime = WorldRugbyDataConverter.getEffectiveTimeFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse)
            rugbyRankerSharedPreferences.setLatestWorldRugbyRankingsEffectiveTime(effectiveTime, sport)
            return true
        }
        return false
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
                    val worldRugbyRankings = WorldRugbyDataConverter.getWorldRugbyRankingsFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse, sport)
                    executor.execute {
                        worldRugbyRankingDao.insert(worldRugbyRankings)
                    }
                    val effectiveTime = WorldRugbyDataConverter.getEffectiveTimeFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse)
                    rugbyRankerSharedPreferences.setLatestWorldRugbyRankingsEffectiveTime(effectiveTime, sport)
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

    private fun getCurrentDate() = DateUtils.getCurrentDate(DateUtils.DATE_FORMAT)

    fun getLatestWorldRugbyRankingsEffectiveTime(sport: Sport): String? {
        return rugbyRankerSharedPreferences.getLatestWorldRugbyRankingsEffectiveTime(sport)
    }

    fun getLatestWorldRugbyRankingsEffectiveTimeLiveData(sport: Sport): LiveData<String> {
        return rugbyRankerSharedPreferences.getLatestWorldRugbyRankingsEffectiveTimeLiveData(sport)
    }
}
