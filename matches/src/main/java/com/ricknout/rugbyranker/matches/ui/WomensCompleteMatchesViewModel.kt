package com.ricknout.rugbyranker.matches.ui

import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.matches.repository.MatchesRepository
import com.ricknout.rugbyranker.matches.work.MatchesWorkManager
import com.ricknout.rugbyranker.rankings.repository.RankingsRepository
import javax.inject.Inject

class WomensCompleteMatchesViewModel @Inject constructor(
    rankingsRepository: RankingsRepository,
    matchesRepository: MatchesRepository,
    matchesWorkManager: MatchesWorkManager
) : MatchesViewModel(Sport.WOMENS, MatchStatus.COMPLETE, rankingsRepository, matchesRepository, matchesWorkManager)
