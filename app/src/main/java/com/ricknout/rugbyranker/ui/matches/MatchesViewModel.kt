package com.ricknout.rugbyranker.ui.matches

import androidx.lifecycle.ViewModel
import com.ricknout.rugbyranker.repository.RugbyRankerRepository
import com.ricknout.rugbyranker.vo.MatchStatus
import com.ricknout.rugbyranker.vo.Sport
import com.ricknout.rugbyranker.work.RugbyRankerWorkManager

open class MatchesViewModel(
        sport: Sport,
        matchStatus: MatchStatus,
        rugbyRankerRepository: RugbyRankerRepository,
        rugbyRankerWorkManager: RugbyRankerWorkManager
) : ViewModel() {

    init {
        rugbyRankerWorkManager.fetchAndStoreLatestWorldRugbyMatches(sport, matchStatus)
    }

    val latestWorldRugbyMatches = rugbyRankerRepository.loadLatestWorldRugbyMatches(sport, matchStatus, asc = matchStatus == MatchStatus.UNPLAYED)
}
