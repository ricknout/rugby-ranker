package com.ricknout.rugbyranker.live.ui

import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.matches.repository.MatchesRepository
import com.ricknout.rugbyranker.rankings.repository.RankingsRepository
import javax.inject.Inject

class WomensLiveMatchesViewModel @Inject constructor(
    rankingsRepository: RankingsRepository,
    matchesRepository: MatchesRepository
) : LiveMatchesViewModel(Sport.WOMENS, rankingsRepository, matchesRepository)
