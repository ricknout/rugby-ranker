package com.ricknout.rugbyranker.ui.matches

import com.ricknout.rugbyranker.repository.RugbyRankerRepository
import com.ricknout.rugbyranker.vo.MatchStatus
import com.ricknout.rugbyranker.vo.Sport
import com.ricknout.rugbyranker.work.RugbyRankerWorkManager
import javax.inject.Inject

class WomensUnplayedMatchesViewModel @Inject constructor(
        rugbyRankerRepository: RugbyRankerRepository,
        rugbyRankerWorkManager: RugbyRankerWorkManager
) : MatchesViewModel(Sport.WOMENS, MatchStatus.UNPLAYED, rugbyRankerRepository, rugbyRankerWorkManager)
