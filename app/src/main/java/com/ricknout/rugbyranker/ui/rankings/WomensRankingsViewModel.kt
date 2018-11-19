package com.ricknout.rugbyranker.ui.rankings

import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.repository.RankingsRepository
import com.ricknout.rugbyranker.work.RugbyRankerWorkManager
import javax.inject.Inject

class WomensRankingsViewModel @Inject constructor(
        rankingsRepository: RankingsRepository,
        rugbyRankerWorkManager: RugbyRankerWorkManager
) : RankingsViewModel(Sport.WOMENS, rankingsRepository, rugbyRankerWorkManager)
