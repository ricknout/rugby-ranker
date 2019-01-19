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
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.random.Random

open class LiveMatchesViewModel(
    private val sport: Sport,
    private val matchesRepository: MatchesRepository
) : ReselectViewModel() {

    private lateinit var timer: Timer

    init {
        startTimer()
    }

    private val matchStatus = MatchStatus.LIVE

    private val _liveWorldRugbyMatches = MutableLiveData<List<WorldRugbyMatch>>()
    val liveWorldRugbyMatches: LiveData<List<WorldRugbyMatch>>
        get() = _liveWorldRugbyMatches

    private val _refreshingLiveWorldRugbyMatches = MutableLiveData<Boolean>().apply { value = false }
    val refreshingLiveWorldRugbyMatches: LiveData<Boolean>
        get() = _refreshingLiveWorldRugbyMatches

    fun refreshLiveWorldRugbyMatches(showRefresh: Boolean = true, onComplete: (success: Boolean) -> Unit) {
        if (showRefresh) _refreshingLiveWorldRugbyMatches.value = true

        // TODO: Implement non-caching version of matchesRepository.fetchAndCacheLatestWorldRugbyMatchesAsync
        val success = Random.nextBoolean()
        if (success) {
            _liveWorldRugbyMatches.postValue(listOf(WorldRugbyMatch(
                    matchId = Random.nextLong(1000L),
                    description = "Description",
                    status = MatchStatus.LIVE,
                    attendance = 100,
                    firstTeamId = 1L,
                    firstTeamName = "First team ${Random.nextInt(100)}",
                    firstTeamAbbreviation = "F${Random.nextInt(100)}",
                    firstTeamScore = Random.nextInt(100),
                    secondTeamId = 2L,
                    secondTeamName = "Second team ${Random.nextInt(100)}",
                    secondTeamAbbreviation = "S${Random.nextInt(100)}",
                    secondTeamScore = Random.nextInt(100),
                    timeLabel = "Time",
                    timeMillis = Random.nextLong(1547887749000L),
                    timeGmtOffset = Random.nextInt(5),
                    venueId = null,
                    venueName = "Venue ${Random.nextInt(100)}",
                    venueCity = "City ${Random.nextInt(100)}",
                    venueCountry = "Country ${Random.nextInt(100)}",
                    eventId = null,
                    eventLabel = "Event ${Random.nextInt(100)}",
                    eventSport = sport,
                    eventRankingsWeight = null,
                    eventStartTimeLabel = null,
                    eventStartTimeMillis = null,
                    eventStartTimeGmtOffset = null,
                    eventEndTimeLabel = null,
                    eventEndTimeMillis = null,
                    eventEndTimeGmtOffset = null
            )))
        }
        onComplete(success)

        if (showRefresh) _refreshingLiveWorldRugbyMatches.value = false
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