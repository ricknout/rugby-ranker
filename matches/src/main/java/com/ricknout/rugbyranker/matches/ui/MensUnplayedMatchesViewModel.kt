package com.ricknout.rugbyranker.matches.ui

import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.matches.repository.MatchesRepository
import com.ricknout.rugbyranker.matches.work.MatchesWorkManager
import javax.inject.Inject

class MensUnplayedMatchesViewModel @Inject constructor(
        matchesRepository: MatchesRepository,
        matchesWorkManager: MatchesWorkManager
) : MatchesViewModel(Sport.MENS, MatchStatus.UNPLAYED, matchesRepository, matchesWorkManager)
