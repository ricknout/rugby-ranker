package com.ricknout.rugbyranker.matches.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.ricknout.rugbyranker.core.livedata.Event
import com.ricknout.rugbyranker.core.viewmodel.ScrollableViewModel
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.matches.repository.MatchesRepository
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch
import com.ricknout.rugbyranker.matches.work.MatchesWorkManager
import com.ricknout.rugbyranker.rankings.repository.RankingsRepository

open class MatchesViewModel(
    private val sport: Sport,
    private val matchStatus: MatchStatus,
    rankingsRepository: RankingsRepository,
    private val matchesRepository: MatchesRepository,
    matchesWorkManager: MatchesWorkManager
) : ScrollableViewModel() {

    init {
        matchesWorkManager.fetchAndStoreLatestWorldRugbyMatches(sport, matchStatus)
    }

    val worldRugbyRankingsTeamIds = rankingsRepository.loadLatestWorldRugbyRankingsTeamIds(sport)

    val latestWorldRugbyMatches = matchesRepository.loadLatestWorldRugbyMatches(sport, matchStatus, asc = matchStatus == MatchStatus.UNPLAYED)

    val latestWorldRugbyMatchesWorkInfos = Transformations.map(
        matchesWorkManager.getLatestWorldRugbyMatchesWorkInfos(sport, matchStatus)
    ) { workInfos ->
        if (matchesRepository.isInitialMatchesFetched(sport, matchStatus)) null else workInfos
    }

    private val _refreshingLatestWorldRugbyMatches = MutableLiveData<Boolean>().apply { value = false }
    val refreshingLatestWorldRugbyMatches: LiveData<Boolean>
        get() = _refreshingLatestWorldRugbyMatches

    fun refreshLatestWorldRugbyMatches(showRefreshing: Boolean = true, onComplete: (success: Boolean) -> Unit) {
        if (showRefreshing) _refreshingLatestWorldRugbyMatches.value = true
        matchesRepository.fetchAndCacheLatestWorldRugbyMatchesAsync(sport, matchStatus, viewModelScope) { success ->
            if (showRefreshing) _refreshingLatestWorldRugbyMatches.value = false
            onComplete(success)
        }
    }

    private val _navigatePredict = MutableLiveData<Event<WorldRugbyMatch>>()
    val navigatePredict: LiveData<Event<WorldRugbyMatch>>
        get() = _navigatePredict

    fun predict(worldRugbyMatch: WorldRugbyMatch) {
        _navigatePredict.value = Event(worldRugbyMatch)
    }
}
