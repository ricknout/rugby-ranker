package com.ricknout.rugbyranker.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.ricknout.rugbyranker.api.WorldRugbyMatchesResponse
import com.ricknout.rugbyranker.api.WorldRugbyRankingsResponse
import com.ricknout.rugbyranker.api.WorldRugbyService
import com.ricknout.rugbyranker.common.util.DateUtils
import com.ricknout.rugbyranker.db.WorldRugbyMatchDao
import com.ricknout.rugbyranker.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.prefs.RugbyRankerSharedPreferences
import com.ricknout.rugbyranker.vo.MatchStatus
import com.ricknout.rugbyranker.vo.Sport
import com.ricknout.rugbyranker.vo.WorldRugbyDataConverter
import com.ricknout.rugbyranker.vo.WorldRugbyMatch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class RugbyRankerRepository(
        private val worldRugbyService: WorldRugbyService,
        private val worldRugbyRankingDao: WorldRugbyRankingDao,
        private val worldRugbyMatchDao: WorldRugbyMatchDao,
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
        try {
            val response = worldRugbyService.getRankings(json, date).execute()
            if (response.isSuccessful) {
                val worldRugbyRankingsResponse = response.body() ?: return false
                val worldRugbyRankings = WorldRugbyDataConverter.getWorldRugbyRankingsFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse, rankingsType)
                executor.execute {
                    worldRugbyRankingDao.insert(worldRugbyRankings)
                }
                rugbyRankerSharedPreferences.setLatestWorldRugbyRankingsEffectiveTimeMillis(worldRugbyRankingsResponse.effective.millis, rankingsType)
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
                    val worldRugbyRankings = WorldRugbyDataConverter.getWorldRugbyRankingsFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse, sport)
                    executor.execute {
                        worldRugbyRankingDao.insert(worldRugbyRankings)
                    }
                    rugbyRankerSharedPreferences.setLatestWorldRugbyRankingsEffectiveTimeMillis(worldRugbyRankingsResponse.effective.millis, sport)
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
        return rugbyRankerSharedPreferences.getLatestWorldRugbyRankingsEffectiveTimeMillis(sport)
    }

    fun getLatestWorldRugbyRankingsEffectiveTimeMillisLiveData(sport: Sport): LiveData<Long> {
        return rugbyRankerSharedPreferences.getLatestWorldRugbyRankingsEffectiveTimeMillisLiveData(sport)
    }

    fun loadLatestWorldRugbyMatches(sport: Sport, matchStatus: MatchStatus, asc: Boolean): LiveData<PagedList<WorldRugbyMatch>> {
        val millis = System.currentTimeMillis()
        val dataSourceFactory = if (asc) worldRugbyMatchDao.loadAsc(sport, matchStatus, millis) else worldRugbyMatchDao.loadDesc(sport, matchStatus, millis)
        val config = Config(pageSize = PAGE_SIZE_WORLD_RUGBY_MATCHES_DATABASE, enablePlaceholders = false)
        return dataSourceFactory.toLiveData(config = config)
    }

    @WorkerThread
    fun fetchAndCacheLatestWorldRugbyMatchesSync(sport: Sport, matchStatus: MatchStatus): Boolean {
        val sports = when (sport) {
            Sport.MENS -> WorldRugbyService.SPORT_MENS
            Sport.WOMENS -> WorldRugbyService.SPORT_WOMENS
        }
        val states = when (matchStatus) {
            MatchStatus.UNPLAYED -> WorldRugbyService.STATE_UNPLAYED
            MatchStatus.COMPLETE -> WorldRugbyService.STATE_COMPLETE
        }
        val millis = System.currentTimeMillis()
        val startDate = when (matchStatus) {
            MatchStatus.UNPLAYED -> DateUtils.getDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, millis)
            MatchStatus.COMPLETE -> DateUtils.getYearBeforeDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, millis)
        }
        val endDate = when (matchStatus) {
            MatchStatus.UNPLAYED -> DateUtils.getYearAfterDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, millis + DateUtils.DAY_MILLIS)
            MatchStatus.COMPLETE -> DateUtils.getDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, millis + DateUtils.DAY_MILLIS)
        }
        val sort = when (matchStatus) {
            MatchStatus.UNPLAYED -> WorldRugbyService.SORT_ASC
            MatchStatus.COMPLETE -> WorldRugbyService.SORT_DESC
        }
        var page = 0
        var pageCount = Int.MAX_VALUE
        var success = false
        while (page < pageCount) {
            val response = worldRugbyService.getMatches(sports, states, startDate, endDate, sort, page, PAGE_SIZE_WORLD_RUGBY_MATCHES_NETWORK).execute()
            if (response.isSuccessful) {
                val worldRugbyMatchesResponse = response.body() ?: break
                val worldRugbyMatches = WorldRugbyDataConverter.getWorldRugbyMatchesFromWorldRugbyMatchesResponse(worldRugbyMatchesResponse, sport)
                executor.execute {
                    worldRugbyMatchDao.insert(worldRugbyMatches)
                }
                page++
                pageCount = worldRugbyMatchesResponse.pageInfo.numPages
                success = true
            } else {
                break
            }
        }
        return success
    }

    fun fetchAndCacheLatestWorldRugbyMatchesAsync(sport: Sport, matchStatus: MatchStatus, onComplete: (success: Boolean) -> Unit) {
        val sports = when (sport) {
            Sport.MENS -> WorldRugbyService.SPORT_MENS
            Sport.WOMENS -> WorldRugbyService.SPORT_WOMENS
        }
        val states = when (matchStatus) {
            MatchStatus.UNPLAYED -> WorldRugbyService.STATE_UNPLAYED
            MatchStatus.COMPLETE -> WorldRugbyService.STATE_COMPLETE
        }
        val millis = System.currentTimeMillis()
        val startDate = when (matchStatus) {
            MatchStatus.UNPLAYED -> DateUtils.getDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, millis)
            MatchStatus.COMPLETE -> DateUtils.getYearBeforeDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, millis)
        }
        val endDate = when (matchStatus) {
            MatchStatus.UNPLAYED -> DateUtils.getYearAfterDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, millis + DateUtils.DAY_MILLIS)
            MatchStatus.COMPLETE -> DateUtils.getDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, millis + DateUtils.DAY_MILLIS)
        }
        val sort = when (matchStatus) {
            MatchStatus.UNPLAYED -> WorldRugbyService.SORT_ASC
            MatchStatus.COMPLETE -> WorldRugbyService.SORT_DESC
        }
        val page = 0
        val callback = object : Callback<WorldRugbyMatchesResponse> {

            override fun onResponse(call: Call<WorldRugbyMatchesResponse>, response: Response<WorldRugbyMatchesResponse>) {
                if (response.isSuccessful) {
                    val worldRugbyMatchesResponse = response.body()
                    if (worldRugbyMatchesResponse == null) {
                        onComplete(false)
                        return
                    }
                    val worldRugbyMatches = WorldRugbyDataConverter.getWorldRugbyMatchesFromWorldRugbyMatchesResponse(worldRugbyMatchesResponse, sport)
                    executor.execute {
                        worldRugbyMatchDao.insert(worldRugbyMatches)
                    }
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }

            override fun onFailure(call: Call<WorldRugbyMatchesResponse>, t: Throwable) {
                onComplete(false)
            }
        }
        worldRugbyService.getMatches(sports, states, startDate, endDate, sort, page, PAGE_SIZE_WORLD_RUGBY_MATCHES_NETWORK).enqueue(callback)
    }

    companion object {
        private const val PAGE_SIZE_WORLD_RUGBY_MATCHES_DATABASE = 20
        private const val PAGE_SIZE_WORLD_RUGBY_MATCHES_NETWORK = 100
    }
}
