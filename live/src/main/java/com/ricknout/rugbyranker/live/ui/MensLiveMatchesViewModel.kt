package com.ricknout.rugbyranker.live.ui

import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.matches.repository.MatchesRepository
import javax.inject.Inject

class MensLiveMatchesViewModel @Inject constructor(
        matchesRepository: MatchesRepository
) : LiveMatchesViewModel(Sport.MENS, matchesRepository)
