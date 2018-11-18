package com.ricknout.rugbyranker.ui.matches

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ricknout.rugbyranker.common.livedata.Event
import com.ricknout.rugbyranker.repository.RugbyRankerRepository
import com.ricknout.rugbyranker.vo.MatchStatus
import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.vo.WorldRugbyMatch
import com.ricknout.rugbyranker.work.RugbyRankerWorkManager

open class MatchesViewModel(
        private val sport: Sport,
        private val matchStatus: MatchStatus,
        private val rugbyRankerRepository: RugbyRankerRepository,
        rugbyRankerWorkManager: RugbyRankerWorkManager
) : ViewModel() {

    init {
        rugbyRankerWorkManager.fetchAndStoreLatestWorldRugbyMatches(sport, matchStatus)
    }

    val latestWorldRugbyMatches = rugbyRankerRepository.loadLatestWorldRugbyMatches(sport, matchStatus, asc = matchStatus == MatchStatus.UNPLAYED)
    val latestWorldRugbyMatchesWorkInfos = rugbyRankerWorkManager.getLatestWorldRugbyMatchesWorkInfos(sport, matchStatus)

    private val _refreshingLatestWorldRugbyMatches = MutableLiveData<Boolean>().apply { value = false }
    val refreshingLatestWorldRugbyMatches: LiveData<Boolean>
        get() = _refreshingLatestWorldRugbyMatches

    fun refreshLatestWorldRugbyMatches(onComplete: (success: Boolean) -> Unit) {
        _refreshingLatestWorldRugbyMatches.value = true
        rugbyRankerRepository.fetchAndCacheLatestWorldRugbyMatchesAsync(sport, matchStatus) { success ->
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
