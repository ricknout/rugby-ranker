package com.ricknout.rugbyranker.matches.repository

import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.ricknout.rugbyranker.core.api.WorldRugbyService
import com.ricknout.rugbyranker.core.util.DateUtils
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.matches.db.WorldRugbyMatchDao
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.matches.vo.MatchesDataConverter
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MatchesRepository(
    private val worldRugbyService: WorldRugbyService,
    private val worldRugbyMatchDao: WorldRugbyMatchDao
) {

    suspend fun hasWorldRugbyMatchesBetween(startMillis: Long, endMillis: Long) = worldRugbyMatchDao.hasBetween(startMillis, endMillis)

    fun loadLatestWorldRugbyMatches(sport: Sport, matchStatus: MatchStatus, asc: Boolean): LiveData<PagedList<WorldRugbyMatch>> {
        val millis = System.currentTimeMillis()
        val dataSourceFactory = if (asc) worldRugbyMatchDao.loadAsc(sport, matchStatus, millis) else worldRugbyMatchDao.loadDesc(sport, matchStatus, millis)
        val config = Config(pageSize = PAGE_SIZE_WORLD_RUGBY_MATCHES_DATABASE, enablePlaceholders = false)
        return dataSourceFactory.toLiveData(config = config)
    }

    suspend fun fetchAndCacheLatestWorldRugbyMatchesSync(sport: Sport, matchStatus: MatchStatus): Boolean {
        val sports = when (sport) {
            Sport.MENS -> WorldRugbyService.SPORT_MENS
            Sport.WOMENS -> WorldRugbyService.SPORT_WOMENS
        }
        val states = when (matchStatus) {
            MatchStatus.UNPLAYED -> WorldRugbyService.STATE_UNPLAYED
            MatchStatus.COMPLETE -> WorldRugbyService.STATE_COMPLETE
            else -> throw IllegalArgumentException("Cannot handle MatchStatus type $matchStatus in fetchAndCacheLatestWorldRugbyMatchesSync")
        }
        val millis = System.currentTimeMillis()
        val startDate = when (matchStatus) {
            MatchStatus.UNPLAYED -> DateUtils.getDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, millis)
            MatchStatus.COMPLETE -> DateUtils.getYearBeforeDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, millis)
            else -> throw IllegalArgumentException("Cannot handle MatchStatus type $matchStatus in fetchAndCacheLatestWorldRugbyMatchesSync")
        }
        val endDate = when (matchStatus) {
            MatchStatus.UNPLAYED -> DateUtils.getYearAfterDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, millis + DateUtils.DAY_MILLIS)
            MatchStatus.COMPLETE -> DateUtils.getDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, millis + DateUtils.DAY_MILLIS)
            else -> throw IllegalArgumentException("Cannot handle MatchStatus type $matchStatus in fetchAndCacheLatestWorldRugbyMatchesSync")
        }
        val sort = when (matchStatus) {
            MatchStatus.UNPLAYED -> WorldRugbyService.SORT_ASC
            MatchStatus.COMPLETE -> WorldRugbyService.SORT_DESC
            else -> throw IllegalArgumentException("Cannot handle MatchStatus type $matchStatus in fetchAndCacheLatestWorldRugbyMatchesSync")
        }
        var page = 0
        var pageCount = Int.MAX_VALUE
        var success = false
        return try {
            while (page < pageCount) {
                val worldRugbyMatchesResponse = worldRugbyService.getMatchesAsync(sports, states, startDate, endDate, sort, page, PAGE_SIZE_WORLD_RUGBY_MATCHES_NETWORK).await()
                val worldRugbyMatches = MatchesDataConverter.getWorldRugbyMatchesFromWorldRugbyMatchesResponse(worldRugbyMatchesResponse, sport)
                worldRugbyMatchDao.insert(worldRugbyMatches)
                page++
                pageCount = worldRugbyMatchesResponse.pageInfo.numPages
                success = true
            }
            success
        } catch (_: Exception) {
            success
        }
    }

    fun fetchAndCacheLatestWorldRugbyMatchesAsync(sport: Sport, matchStatus: MatchStatus, coroutineScope: CoroutineScope, onComplete: (success: Boolean) -> Unit) {
        if (matchStatus == MatchStatus.LIVE) throw IllegalArgumentException("Cannot handle MatchStatus type $matchStatus in fetchAndCacheLatestWorldRugbyMatchesSync")
        coroutineScope.launch(Dispatchers.IO) {
            val refresh = refreshLatestWorldRugbyMatchesAsync(sport, matchStatus, cache = true)
            val success = refresh.first
            withContext(Dispatchers.Main) { onComplete(success) }
        }
    }

    fun fetchLatestWorldRugbyMatchesAsync(sport: Sport, matchStatus: MatchStatus, coroutineScope: CoroutineScope, onComplete: (success: Boolean, worldRugbyMatches: List<WorldRugbyMatch>) -> Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val refresh = refreshLatestWorldRugbyMatchesAsync(sport, matchStatus, cache = false)
            val success = refresh.first
            val worldRugbyMatches = refresh.second
            withContext(Dispatchers.Main) { onComplete(success, worldRugbyMatches) }
        }
    }

    private suspend fun refreshLatestWorldRugbyMatchesAsync(sport: Sport, matchStatus: MatchStatus, cache: Boolean): Pair<Boolean, List<WorldRugbyMatch>> {
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
        val page = 0
        return try {
            val worldRugbyMatchesResponse = worldRugbyService.getMatchesAsync(sports, states, startDate, endDate, sort, page, PAGE_SIZE_WORLD_RUGBY_MATCHES_NETWORK).await()
            val worldRugbyMatches = MatchesDataConverter.getWorldRugbyMatchesFromWorldRugbyMatchesResponse(worldRugbyMatchesResponse, sport)
            if (cache) worldRugbyMatchDao.insert(worldRugbyMatches)
            true to worldRugbyMatches
        } catch (_: Exception) {
            false to emptyList()
        }
    }

    companion object {
        private const val PAGE_SIZE_WORLD_RUGBY_MATCHES_DATABASE = 20
        private const val PAGE_SIZE_WORLD_RUGBY_MATCHES_NETWORK = 100
    }
}
