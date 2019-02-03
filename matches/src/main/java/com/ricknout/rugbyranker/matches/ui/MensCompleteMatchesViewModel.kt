package com.ricknout.rugbyranker.matches.ui

import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.matches.repository.MatchesRepository
import com.ricknout.rugbyranker.matches.work.MatchesWorkManager
import com.ricknout.rugbyranker.rankings.repository.RankingsRepository
import javax.inject.Inject

class MensCompleteMatchesViewModel @Inject constructor(
    rankingsRepository: RankingsRepository,
    matchesRepository: MatchesRepository,
    matchesWorkManager: MatchesWorkManager
) : MatchesViewModel(Sport.MENS, MatchStatus.COMPLETE, rankingsRepository, matchesRepository, matchesWorkManager)
