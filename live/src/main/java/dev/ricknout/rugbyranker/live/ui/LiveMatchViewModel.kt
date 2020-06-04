package dev.ricknout.rugbyranker.live.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.ricknout.rugbyranker.core.lifecycle.ScrollableViewModel
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.core.util.DateUtils
import dev.ricknout.rugbyranker.match.data.MatchRepository
import dev.ricknout.rugbyranker.match.model.Match
import dev.ricknout.rugbyranker.match.model.Status
import dev.ricknout.rugbyranker.prediction.model.Prediction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class LiveMatchViewModel(
    private val sport: Sport,
    private val repository: MatchRepository
) : ScrollableViewModel() {

    private val status = Status.LIVE

    private val _liveMatches = MutableStateFlow<List<Match>?>(null)
    val liveMatches: LiveData<List<Match>?> = _liveMatches.asLiveData()

    private val _refreshingLiveMatches = MutableStateFlow(false)
    val refreshingLiveMatches: LiveData<Boolean> = _refreshingLiveMatches.asLiveData()

    private val _predict = MutableStateFlow<Prediction?>(null)
    val predict: LiveData<Prediction?> = _predict.asLiveData()

    private var unplayedMatchesToday: Boolean? = null

    init {
        startLiveMatchesRefreshJob()
    }

    fun refreshLiveMatches(showRefresh: Boolean = true, onComplete: (success: Boolean) -> Unit) {
        if (showRefresh) _refreshingLiveMatches.value = true
        repository.fetchLatestMatchesAsync(sport, status, viewModelScope) { success, matches ->
            _liveMatches.value = matches
            if (showRefresh) _refreshingLiveMatches.value = false
            onComplete(success)
        }
    }

    private fun startLiveMatchesRefreshJob() {
        viewModelScope.launch {
            val unplayedMatchesToday = unplayedMatchesToday ?: withContext(Dispatchers.IO) {
                repository.hasUnplayedMatchesToday(sport)
            }
            this@LiveMatchViewModel.unplayedMatchesToday = unplayedMatchesToday
            val refreshedLiveMatchesOnce = _liveMatches.value != null
            val ongoingLiveMatches = !_liveMatches.value.isNullOrEmpty()
            if (!refreshedLiveMatchesOnce || ongoingLiveMatches || unplayedMatchesToday) {
                refreshLiveMatches(showRefresh = false) {
                    // Do nothing, no need to notify of success / failure here
                }
            } else {
                _liveMatches.value = emptyList()
            }
            delay(DateUtils.MINUTE_MILLIS)
            startLiveMatchesRefreshJob()
        }
    }

    fun predict(prediction: Prediction) {
        _predict.value = prediction
    }

    fun resetPredict() {
        _predict.value = null
    }
}
