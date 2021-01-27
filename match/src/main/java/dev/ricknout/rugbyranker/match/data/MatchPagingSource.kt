package dev.ricknout.rugbyranker.match.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.core.util.DateUtils
import dev.ricknout.rugbyranker.match.model.Match
import dev.ricknout.rugbyranker.match.model.Status

class MatchPagingSource(
    private val sport: Sport,
    private val status: Status,
    private val repository: MatchRepository
) : PagingSource<Int, Match>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Match> {
        val page = params.key ?: 0
        val pageSize = params.loadSize
        val currentDate = DateUtils.getCurrentDate(DateUtils.DATE_FORMAT_YYYY_MM_DD)
        val (success, matches) = repository.fetchLatestMatchesSync(
            sport,
            status,
            page = page,
            pageSize = pageSize,
            unplayedStartDate = currentDate
        )
        return if (success) {
            LoadResult.Page(
                data = matches,
                prevKey = if (page == 0) null else page.dec(),
                nextKey = if (matches.isEmpty()) null else page.inc()
            )
        } else {
            LoadResult.Error(RuntimeException("Failed to fetch matches with page = $page and page size = $pageSize"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Match>): Int? = null
}
