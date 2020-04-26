package com.ricknout.rugbyranker.matches.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.ricknout.rugbyranker.core.api.WorldRugbyService
import com.ricknout.rugbyranker.core.util.DateUtils
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.matches.db.WorldRugbyMatchDao
import com.ricknout.rugbyranker.matches.prefs.MatchesSharedPreferences
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.matches.vo.MatchesDataConverter
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MatchesRepository(
    private val worldRugbyService: WorldRugbyService,
    private val worldRugbyMatchDao: WorldRugbyMatchDao,
    private val matchesSharedPreferences: MatchesSharedPreferences
) {

    suspend fun hasWorldRugbyMatchesBetween(startMillis: Long, endMillis: Long) = worldRugbyMatchDao.hasBetween(startMillis, endMillis)

    fun loadLatestWorldRugbyMatches(sport: Sport, matchStatus: MatchStatus, asc: Boolean): LiveData<PagedList<WorldRugbyMatch>> {
        val millis = System.currentTimeMillis()
        val dataSourceFactory = if (asc) worldRugbyMatchDao.loadAsc(sport, matchStatus, millis) else worldRugbyMatchDao.loadDesc(sport, matchStatus, millis)
        val config = Config(pageSize = PAGE_SIZE_WORLD_RUGBY_MATCHES_DATABASE, enablePlaceholders = false)
        return dataSourceFactory.toLiveData(config = config)
    }

    fun isInitialMatchesFetched(sport: Sport, matchStatus: MatchStatus) =
            matchesSharedPreferences.isInitialMatchesFetched(sport, matchStatus)

    suspend fun fetchAndCacheLatestWorldRugbyMatchesSync(
        sport: Sport,
        matchStatus: MatchStatus,
        cache: Boolean = true,
        pageSize: Int = PAGE_SIZE_WORLD_RUGBY_MATCHES_NETWORK,
        fetchMultiplePages: Boolean = true,
        fetchMinutes: Boolean = false
    ): Pair<Boolean, List<WorldRugbyMatch>> {
        val sports = when (sport) {
            Sport.MENS -> WorldRugbyService.SPORT_MENS
            Sport.WOMENS -> WorldRugbyService.SPORT_WOMENS
        }
        val states = when (matchStatus) {
            MatchStatus.UNPLAYED -> WorldRugbyService.STATE_UNPLAYED
            MatchStatus.COMPLETE -> WorldRugbyService.STATE_COMPLETE
            MatchStatus.LIVE -> "${WorldRugbyService.STATE_LIVE_1ST_HALF},${WorldRugbyService.STATE_LIVE_2ND_HALF},${WorldRugbyService.STATE_LIVE_HALF_TIME}"
        }
        val millis = System.currentTimeMillis()
        val startDate = when (matchStatus) {
            MatchStatus.UNPLAYED -> DateUtils.getDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, millis)
            MatchStatus.COMPLETE -> DateUtils.getYearBeforeDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, millis)
            MatchStatus.LIVE -> ""
        }
        val endDate = when (matchStatus) {
            MatchStatus.UNPLAYED -> DateUtils.getYearAfterDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, millis + DateUtils.DAY_MILLIS)
            MatchStatus.COMPLETE -> DateUtils.getDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, millis + DateUtils.DAY_MILLIS)
            MatchStatus.LIVE -> ""
        }
        val sort = when (matchStatus) {
            MatchStatus.UNPLAYED, MatchStatus.LIVE -> WorldRugbyService.SORT_ASC
            MatchStatus.COMPLETE -> WorldRugbyService.SORT_DESC
        }
        var page = 0
        var pageCount = Int.MAX_VALUE
        var success = false
        val worldRugbyMatches = mutableListOf<WorldRugbyMatch>()
        val initialMatchesFetched = isInitialMatchesFetched(sport, matchStatus)
        return try {
            while (page < pageCount) {
                val worldRugbyMatchesResponse = worldRugbyService.getMatches(sports, states, startDate, endDate, sort, page, pageSize)
                val matches = MatchesDataConverter.getWorldRugbyMatchesFromWorldRugbyMatchesResponse(worldRugbyMatchesResponse, sport).map { match ->
                    if (fetchMinutes) {
                        val worldRugbyMatchSummaryResponse = worldRugbyService.getMatchSummary(match.matchId)
                        val minute = MatchesDataConverter.getMinuteFromWorldRugbyMatchSummaryResponse(worldRugbyMatchSummaryResponse)
                        match.copy(minute = minute)
                    } else {
                        match
                    }
                }
                if (cache) worldRugbyMatchDao.insert(matches)
                page++
                pageCount = if (fetchMultiplePages && !initialMatchesFetched) worldRugbyMatchesResponse.pageInfo.numPages else 1
                success = true
                worldRugbyMatches.addAll(matches)
            }
            if (fetchMultiplePages) matchesSharedPreferences.setInitialMatchesFetched(sport, matchStatus, true)
            success to worldRugbyMatches
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            success to worldRugbyMatches
        }
    }

    fun fetchAndCacheLatestWorldRugbyMatchesAsync(sport: Sport, matchStatus: MatchStatus, coroutineScope: CoroutineScope, onComplete: (success: Boolean) -> Unit) {
        if (matchStatus == MatchStatus.LIVE) throw IllegalArgumentException("Cannot handle MatchStatus type $matchStatus in fetchAndCacheLatestWorldRugbyMatchesSync")
        coroutineScope.launch {
            val result = withContext(Dispatchers.IO) {
                fetchAndCacheLatestWorldRugbyMatchesSync(
                        sport, matchStatus, cache = true, pageSize = PAGE_SIZE_WORLD_RUGBY_MATCHES_NETWORK_REFRESH, fetchMultiplePages = false, fetchMinutes = false)
            }
            val success = result.first
            onComplete(success)
        }
    }

    fun fetchLatestWorldRugbyMatchesAsync(sport: Sport, matchStatus: MatchStatus, coroutineScope: CoroutineScope, onComplete: (success: Boolean, worldRugbyMatches: List<WorldRugbyMatch>) -> Unit) {
        coroutineScope.launch {
            val result = withContext(Dispatchers.IO) {
                fetchAndCacheLatestWorldRugbyMatchesSync(
                        sport, matchStatus, cache = false, pageSize = PAGE_SIZE_WORLD_RUGBY_MATCHES_NETWORK_REFRESH, fetchMultiplePages = false, fetchMinutes = true)
            }
            val success = result.first
            val worldRugbyMatches = result.second
            onComplete(success, worldRugbyMatches)
        }
    }

    companion object {
        private const val TAG = "MatchesRepository"
        private const val PAGE_SIZE_WORLD_RUGBY_MATCHES_DATABASE = 20
        private const val PAGE_SIZE_WORLD_RUGBY_MATCHES_NETWORK = 100
        private const val PAGE_SIZE_WORLD_RUGBY_MATCHES_NETWORK_REFRESH = 20
    }
}
