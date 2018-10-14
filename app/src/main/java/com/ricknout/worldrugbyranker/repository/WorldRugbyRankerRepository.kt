package com.ricknout.worldrugbyranker.repository

import androidx.lifecycle.LiveData
import com.ricknout.worldrugbyranker.AppExecutors
import com.ricknout.worldrugbyranker.api.WorldRugbyRankingsResponse
import com.ricknout.worldrugbyranker.api.WorldRugbyRankingsService
import com.ricknout.worldrugbyranker.api.WorldRugbyRankingsService.Companion.JSON_MENS
import com.ricknout.worldrugbyranker.api.WorldRugbyRankingsService.Companion.JSON_WOMENS
import com.ricknout.worldrugbyranker.db.WorldRugbyRankingDao
import com.ricknout.worldrugbyranker.prefs.WorldRugbyRankerSharedPreferences
import com.ricknout.worldrugbyranker.util.DateUtils
import com.ricknout.worldrugbyranker.vo.RankingsType
import com.ricknout.worldrugbyranker.vo.WorldRugbyRanking
import com.ricknout.worldrugbyranker.vo.WorldRugbyRankingDataConverter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorldRugbyRankerRepository(
        private val worldRugbyRankingsService: WorldRugbyRankingsService,
        private val worldRugbyRankingDao: WorldRugbyRankingDao,
        private val worldRugbyRankerSharedPreferences: WorldRugbyRankerSharedPreferences,
        private val appExecutors: AppExecutors
) {

    fun getLatestWorldRugbyRankings(rankingsType: RankingsType): LiveData<List<WorldRugbyRanking>> {
        val currentDate = getCurrentDate()
        return getWorldRugbyRankings(rankingsType, currentDate)
    }

    private fun getWorldRugbyRankings(rankingsType: RankingsType, date: String): LiveData<List<WorldRugbyRanking>> {
        refreshWorldRugbyRankings(rankingsType, date)
        return worldRugbyRankingDao.load(rankingsType)
    }

    private fun refreshWorldRugbyRankings(rankingsType: RankingsType, date: String) {
        val refreshTime = worldRugbyRankerSharedPreferences.getRefreshTime(rankingsType)
        if (!shouldRefresh(refreshTime)) return
        appExecutors.networkIO.execute {
            val callback = object : Callback<WorldRugbyRankingsResponse> {

                override fun onResponse(call: Call<WorldRugbyRankingsResponse>, response: Response<WorldRugbyRankingsResponse>) {
                    if (response.isSuccessful) {
                        val worldRugbyRankingsResponse = response.body() ?: return
                        val worldRugbyRankings = WorldRugbyRankingDataConverter.convertFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse, rankingsType)
                        appExecutors.diskIO.execute {
                            worldRugbyRankingDao.insert(worldRugbyRankings)
                        }
                        worldRugbyRankerSharedPreferences.setRefreshTime(rankingsType, System.currentTimeMillis())
                    }
                    // TODO: Handle unsuccessful?
                }

                override fun onFailure(call: Call<WorldRugbyRankingsResponse>, t: Throwable) {
                    // TODO: Handle failure?
                }
            }
            val json = when (rankingsType) {
                RankingsType.MENS -> JSON_MENS
                RankingsType.WOMENS -> JSON_WOMENS
            }
            worldRugbyRankingsService.getWorldRugbyRankings(json, date).enqueue(callback)
        }
    }

    private fun shouldRefresh(refreshTime: Long) = System.currentTimeMillis() - refreshTime >= TIMEOUT_MILLIS

    private fun getCurrentDate(): String = DateUtils.getCurrentDate(DATE_FORMAT)

    companion object {
        private const val TIMEOUT_MILLIS = 1000L * 60L * 60L * 24L
        private const val DATE_FORMAT = "yyyy-MM-dd"
    }
}
