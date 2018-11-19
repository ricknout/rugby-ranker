package com.ricknout.rugbyranker.ui.matches

import com.ricknout.rugbyranker.vo.MatchStatus
import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.repository.MatchesRepository
import com.ricknout.rugbyranker.work.MatchesWorkManager
import javax.inject.Inject

class MensUnplayedMatchesViewModel @Inject constructor(
        matchesRepository: MatchesRepository,
        matchesWorkManager: MatchesWorkManager
) : MatchesViewModel(Sport.MENS, MatchStatus.UNPLAYED, matchesRepository, matchesWorkManager)
