package com.ricknout.rugbyranker.live.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ricknout.rugbyranker.common.livedata.Event
import com.ricknout.rugbyranker.common.util.DateUtils
import com.ricknout.rugbyranker.common.viewmodel.ReselectViewModel
import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.matches.repository.MatchesRepository
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch
import com.ricknout.rugbyranker.rankings.repository.RankingsRepository
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

open class LiveMatchesViewModel(
    private val sport: Sport,
    rankingsRepository: RankingsRepository,
    private val matchesRepository: MatchesRepository
) : ReselectViewModel() {

    private lateinit var timer: Timer

    init {
        startTimer()
    }

    private val matchStatus = MatchStatus.LIVE

    val worldRugbyRankingsTeamIds = rankingsRepository.loadLatestWorldRugbyRankingsTeamIds(sport)

    private val _liveWorldRugbyMatches = MutableLiveData<List<WorldRugbyMatch>>()
    val liveWorldRugbyMatches: LiveData<List<WorldRugbyMatch>>
        get() = _liveWorldRugbyMatches

    private val _refreshingLiveWorldRugbyMatches = MutableLiveData<Boolean>().apply { value = false }
    val refreshingLiveWorldRugbyMatches: LiveData<Boolean>
        get() = _refreshingLiveWorldRugbyMatches

    fun refreshLiveWorldRugbyMatches(showRefresh: Boolean = true, onComplete: (success: Boolean) -> Unit) {
        if (showRefresh) _refreshingLiveWorldRugbyMatches.value = true
        matchesRepository.fetchLatestWorldRugbyMatchesAsync(sport, matchStatus) { success, worldRugbyMatches ->
            _liveWorldRugbyMatches.postValue(worldRugbyMatches)
            if (showRefresh) _refreshingLiveWorldRugbyMatches.value = false
            onComplete(success)
        }
    }

    private fun startTimer() {
        timer = Timer(TIMER_NAME)
        timer.scheduleAtFixedRate(0, DateUtils.MINUTE_MILLIS) {
            refreshLiveWorldRugbyMatches(showRefresh = false) {
                // Do nothing, no need to notify of success / failure here
            }
        }
    }

    private fun stopTimer() {
        timer.cancel()
    }

    private val _navigatePredict = MutableLiveData<Event<WorldRugbyMatch>>()
    val navigatePredict: LiveData<Event<WorldRugbyMatch>>
        get() = _navigatePredict

    fun predict(worldRugbyMatch: WorldRugbyMatch) {
        _navigatePredict.value = Event(worldRugbyMatch)
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }

    companion object {
        private const val TIMER_NAME = "live_matches_timer"
    }
}