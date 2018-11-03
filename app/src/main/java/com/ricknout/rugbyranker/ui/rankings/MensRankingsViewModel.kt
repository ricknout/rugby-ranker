package com.ricknout.rugbyranker.ui.rankings

import com.ricknout.rugbyranker.repository.RugbyRankerRepository
import com.ricknout.rugbyranker.vo.Sport
import com.ricknout.rugbyranker.work.RugbyRankerWorkManager
import javax.inject.Inject

class MensRankingsViewModel @Inject constructor(
        rugbyRankerRepository: RugbyRankerRepository,
        rugbyRankerWorkManager: RugbyRankerWorkManager
) : RankingsViewModel(Sport.MENS, rugbyRankerRepository, rugbyRankerWorkManager)
