package com.ricknout.rugbyranker.live.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ricknout.rugbyranker.common.livedata.Event
import com.ricknout.rugbyranker.common.util.DateUtils
import com.ricknout.rugbyranker.common.viewmodel.ReselectViewModel
import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.matches.repository.MatchesRepository
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch
import com.ricknout.rugbyranker.rankings.repository.RankingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class LiveMatchesViewModel(
    private val sport: Sport,
    rankingsRepository: RankingsRepository,
    private val matchesRepository: MatchesRepository
) : ReselectViewModel() {

    private val matchStatus = MatchStatus.LIVE

    val worldRugbyRankingsTeamIds = rankingsRepository.loadLatestWorldRugbyRankingsTeamIds(sport)

    private val _liveWorldRugbyMatches = MutableLiveData<List<WorldRugbyMatch>>()
    val liveWorldRugbyMatches: LiveData<List<WorldRugbyMatch>>
        get() = _liveWorldRugbyMatches

    private val _refreshingLiveWorldRugbyMatches = MutableLiveData<Boolean>().apply { value = false }
    val refreshingLiveWorldRugbyMatches: LiveData<Boolean>
        get() = _refreshingLiveWorldRugbyMatches

    private val _navigatePredict = MutableLiveData<Event<WorldRugbyMatch>>()
    val navigatePredict: LiveData<Event<WorldRugbyMatch>>
        get() = _navigatePredict

    init {
        startRefreshJob(REFRESH_INTERVAL)
    }

    fun refreshLiveWorldRugbyMatches(showRefresh: Boolean = true, onComplete: (success: Boolean) -> Unit) {
        if (showRefresh) _refreshingLiveWorldRugbyMatches.value = true
        matchesRepository.fetchLatestWorldRugbyMatchesAsync(sport, matchStatus, viewModelScope) { success, worldRugbyMatches ->
            _liveWorldRugbyMatches.postValue(worldRugbyMatches)
            if (showRefresh) _refreshingLiveWorldRugbyMatches.postValue(false)
            onComplete(success)
        }
    }

    private fun startRefreshJob(delayTimeMillis: Long) {
        viewModelScope.launch {
            val hasNotRefreshedLiveWorldRugbyMatchesOnce = liveWorldRugbyMatches.value == null
            val hasOngoingLiveWorldRugbyMatches = !liveWorldRugbyMatches.value.isNullOrEmpty()
            val currentTimeMillis = System.currentTimeMillis()
            val hasScheduledWorldRugbyMatches = matchesRepository.hasMatchesBetween(
                    currentTimeMillis - MATCH_PERIOD_BEFORE,
                    currentTimeMillis + MATCH_PERIOD_AFTER
            )
            if (hasNotRefreshedLiveWorldRugbyMatchesOnce || hasOngoingLiveWorldRugbyMatches || hasScheduledWorldRugbyMatches) {
                refreshLiveWorldRugbyMatches(showRefresh = false) {
                    // Do nothing, no need to notify of success / failure here
                }
            } else {
                _liveWorldRugbyMatches.postValue(emptyList())
            }
            delay(delayTimeMillis)
            startRefreshJob(delayTimeMillis)
        }
    }

    fun predict(worldRugbyMatch: WorldRugbyMatch) {
        _navigatePredict.value = Event(worldRugbyMatch)
    }

    companion object {
        private const val REFRESH_INTERVAL = DateUtils.MINUTE_MILLIS
        private const val MATCH_PERIOD_BEFORE = 2 * DateUtils.HOUR_MILLIS
        private const val MATCH_PERIOD_AFTER = 5 * DateUtils.MINUTE_MILLIS
    }
}