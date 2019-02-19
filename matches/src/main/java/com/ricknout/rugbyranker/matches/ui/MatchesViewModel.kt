package com.ricknout.rugbyranker.matches.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ricknout.rugbyranker.core.livedata.Event
import com.ricknout.rugbyranker.core.viewmodel.ReselectViewModel
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.matches.repository.MatchesRepository
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch
import com.ricknout.rugbyranker.matches.work.MatchesWorkManager
import com.ricknout.rugbyranker.rankings.repository.RankingsRepository

open class MatchesViewModel(
    private val sport: Sport,
    private val matchStatus: MatchStatus,
    rankingsRepository: RankingsRepository,
    private val matchesRepository: MatchesRepository,
    matchesWorkManager: MatchesWorkManager
) : ReselectViewModel() {

    init {
        matchesWorkManager.fetchAndStoreLatestWorldRugbyMatches(sport, matchStatus)
    }

    val worldRugbyRankingsTeamIds = rankingsRepository.loadLatestWorldRugbyRankingsTeamIds(sport)

    val latestWorldRugbyMatches = matchesRepository.loadLatestWorldRugbyMatches(sport, matchStatus, asc = matchStatus == MatchStatus.UNPLAYED)
    val latestWorldRugbyMatchesWorkInfos = matchesWorkManager.getLatestWorldRugbyMatchesWorkInfos(sport, matchStatus)

    private val _refreshingLatestWorldRugbyMatches = MutableLiveData<Boolean>().apply { value = false }
    val refreshingLatestWorldRugbyMatches: LiveData<Boolean>
        get() = _refreshingLatestWorldRugbyMatches

    fun refreshLatestWorldRugbyMatches(onComplete: (success: Boolean) -> Unit) {
        _refreshingLatestWorldRugbyMatches.value = true
        matchesRepository.fetchAndCacheLatestWorldRugbyMatchesAsync(sport, matchStatus, viewModelScope) { success ->
            _refreshingLatestWorldRugbyMatches.value = false
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
