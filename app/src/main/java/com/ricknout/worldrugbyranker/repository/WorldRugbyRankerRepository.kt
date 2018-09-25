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
import com.ricknout.worldrugbyranker.vo.MensWorldRugbyRanking
import com.ricknout.worldrugbyranker.vo.WomensWorldRugbyRanking
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

    fun getLatestMensWorldRugbyRankings(): LiveData<List<MensWorldRugbyRanking>> {
        val currentDate = getCurrentDate()
        return getMensWorldRugbyRankings(currentDate)
    }

    private fun getMensWorldRugbyRankings(date: String): LiveData<List<MensWorldRugbyRanking>> {
        refreshMensWorldRugbyRankings(date)
        return worldRugbyRankingDao.loadMens()
    }

    private fun refreshMensWorldRugbyRankings(date: String) {
        val mensRefreshTime = worldRugbyRankerSharedPreferences.getMensRefreshTime()
        if (!shouldRefresh(mensRefreshTime)) return
        appExecutors.networkIO.execute {
            val callback = object : Callback<WorldRugbyRankingsResponse> {

                override fun onResponse(call: Call<WorldRugbyRankingsResponse>, response: Response<WorldRugbyRankingsResponse>) {
                    if (response.isSuccessful) {
                        val worldRugbyRankingsResponse = response.body() ?: return
                        val worldRugbyRankings = WorldRugbyRankingDataConverter.convertFromMensWorldRugbyRankingsResponse(worldRugbyRankingsResponse)
                        appExecutors.diskIO.execute {
                            worldRugbyRankingDao.insertMens(worldRugbyRankings)
                        }
                        worldRugbyRankerSharedPreferences.setMensRefreshTime(System.currentTimeMillis())
                    }
                    // TODO: Handle unsuccessful
                }

                override fun onFailure(call: Call<WorldRugbyRankingsResponse>, t: Throwable) {
                    // TODO: Handle failure
                }
            }
            worldRugbyRankingsService.getWorldRugbyRankings(JSON_MENS, date).enqueue(callback)
        }
    }

    fun getLatestWomensWorldRugbyRankings(): LiveData<List<WomensWorldRugbyRanking>> {
        val currentDate = getCurrentDate()
        return getWomensWorldRugbyRankings(currentDate)
    }

    private fun getWomensWorldRugbyRankings(date: String): LiveData<List<WomensWorldRugbyRanking>> {
        refreshWomensWorldRugbyRankings(date)
        return worldRugbyRankingDao.loadWomens()
    }

    private fun refreshWomensWorldRugbyRankings(date: String) {
        val womensRefreshTime = worldRugbyRankerSharedPreferences.getWomensRefreshTime()
        if (!shouldRefresh(womensRefreshTime)) return
        appExecutors.networkIO.execute {
            val callback = object : Callback<WorldRugbyRankingsResponse> {

                override fun onResponse(call: Call<WorldRugbyRankingsResponse>, response: Response<WorldRugbyRankingsResponse>) {
                    if (response.isSuccessful) {
                        val worldRugbyRankingsResponse = response.body() ?: return
                        val worldRugbyRankings = WorldRugbyRankingDataConverter.convertFromWomensWorldRugbyRankingsResponse(worldRugbyRankingsResponse)
                        appExecutors.diskIO.execute {
                            worldRugbyRankingDao.insertWomens(worldRugbyRankings)
                        }
                        worldRugbyRankerSharedPreferences.setWomensRefreshTime(System.currentTimeMillis())
                    }
                    // TODO: Handle unsuccessful
                }

                override fun onFailure(call: Call<WorldRugbyRankingsResponse>, t: Throwable) {
                    // TODO: Handle failure
                }
            }
            worldRugbyRankingsService.getWorldRugbyRankings(JSON_WOMENS, date).enqueue(callback)
        }
    }

    private fun shouldRefresh(refreshTime: Long) = System.currentTimeMillis() - refreshTime >= TIMEOUT_MILLIS

    private fun getCurrentDate(): String = DateUtils.getCurrentDate(DATE_FORMAT)

    companion object {
        private const val TIMEOUT_MILLIS = 1000L * 60L * 60L * 24L
        private const val DATE_FORMAT = "yyyy-MM-dd"
    }
}