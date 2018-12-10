package com.ricknout.rugbyranker.rankings.ui

import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.rankings.repository.RankingsRepository
import com.ricknout.rugbyranker.rankings.work.RankingsWorkManager
import javax.inject.Inject

class MensRankingsViewModel @Inject constructor(
        rankingsRepository: RankingsRepository,
        rankingsWorkManager: RankingsWorkManager
) : RankingsViewModel(Sport.MENS, rankingsRepository, rankingsWorkManager)
