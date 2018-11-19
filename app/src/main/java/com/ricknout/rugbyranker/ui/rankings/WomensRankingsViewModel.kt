package com.ricknout.rugbyranker.ui.rankings

import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.repository.RankingsRepository
import com.ricknout.rugbyranker.work.RankingsWorkManager
import javax.inject.Inject

class WomensRankingsViewModel @Inject constructor(
        rankingsRepository: RankingsRepository,
        rankingsWorkManager: RankingsWorkManager
) : RankingsViewModel(Sport.WOMENS, rankingsRepository, rankingsWorkManager)
