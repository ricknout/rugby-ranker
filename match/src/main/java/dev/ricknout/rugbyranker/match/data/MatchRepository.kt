package dev.ricknout.rugbyranker.match.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.ricknout.rugbyranker.core.api.WorldRugbyService
import dev.ricknout.rugbyranker.core.db.RankingDao
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.core.util.DateUtils
import dev.ricknout.rugbyranker.match.model.Match
import dev.ricknout.rugbyranker.match.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MatchRepository(
    private val service: WorldRugbyService,
    private val dao: RankingDao
) {

    fun loadMatches(sport: Sport, status: Status): Flow<PagingData<Match>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = { MatchPagingSource(sport, status, this) }
        ).flow
    }

    suspend fun fetchLatestMatchesSync(
        sport: Sport,
        status: Status,
        page: Int,
        pageSize: Int,
        unplayedStartDate: String? = null,
        unplayedEndDate: String? = null
    ): Pair<Boolean, List<Match>> {
        val sports = when (sport) {
            Sport.MENS -> WorldRugbyService.SPORT_MENS
            Sport.WOMENS -> WorldRugbyService.SPORT_WOMENS
        }
        val states = when (status) {
            Status.UNPLAYED -> WorldRugbyService.STATE_UNPLAYED
            Status.COMPLETE -> WorldRugbyService.STATE_COMPLETE
            Status.LIVE -> WorldRugbyService.STATE_LIVE
            else -> throw IllegalArgumentException("Cannot handle $status in fetchLatestMatchesSync")
        }
        val startDate = when (status) {
            Status.UNPLAYED -> unplayedStartDate ?: ""
            else -> ""
        }
        val endDate = when (status) {
            Status.UNPLAYED -> unplayedEndDate ?: ""
            else -> ""
        }
        val sort = when (status) {
            Status.UNPLAYED, Status.LIVE -> WorldRugbyService.SORT_ASC
            Status.COMPLETE -> WorldRugbyService.SORT_DESC
            else -> throw IllegalArgumentException("Cannot handle $status in fetchLatestMatchesSync")
        }
        return try {
            val response = service.getMatches(sports, states, startDate, endDate, sort, page, pageSize)
            val teamIds = dao.loadTeamIds(sport)
            val matches = MatchDataConverter.getMatchesFromResponse(response, sport, teamIds).map { match ->
                when (match.status) {
                    Status.LIVE -> {
                        val summaryResponse = service.getMatchSummary(match.id)
                        val minute = MatchDataConverter.getMinuteFromResponse(summaryResponse)
                        match.copy(minute = minute)
                    }
                    else -> match
                }
            }.run {
                if (sort == WorldRugbyService.SORT_ASC) {
                    sortedBy { match -> match.timeMillis }
                } else {
                    sortedByDescending { match -> match.timeMillis }
                }
            }
            true to matches
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            false to emptyList()
        }
    }

    fun fetchLatestMatchesAsync(
        sport: Sport,
        status: Status,
        coroutineScope: CoroutineScope,
        onComplete: (success: Boolean, matches: List<Match>) -> Unit
    ) = coroutineScope.launch {
        val currentDate = DateUtils.getCurrentDate(DateUtils.DATE_FORMAT_YYYY_MM_DD)
        val result = withContext(Dispatchers.IO) {
            fetchLatestMatchesSync(
                sport,
                status,
                page = 0,
                pageSize = PAGE_SIZE,
                unplayedStartDate = currentDate
            )
        }
        val success = result.first
        val matches = result.second
        onComplete(success, matches)
    }

    suspend fun hasUnplayedMatchesToday(sport: Sport): Boolean {
        val currentDate = DateUtils.getCurrentDate(DateUtils.DATE_FORMAT_YYYY_MM_DD)
        val result = fetchLatestMatchesSync(
            sport,
            Status.UNPLAYED,
            page = 0,
            pageSize = PAGE_SIZE_MAX,
            unplayedStartDate = currentDate,
            unplayedEndDate = currentDate
        )
        val success = result.first
        val matches = result.second
        return success && matches.firstOrNull { match -> match.status == Status.UNPLAYED } != null
    }

    companion object {
        private const val TAG = "MatchRepository"
        private const val PAGE_SIZE = 20
        private const val PAGE_SIZE_MAX = 100
    }
}
