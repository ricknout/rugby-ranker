package com.ricknout.rugbyranker.ui.rankings

import com.ricknout.rugbyranker.repository.RugbyRankerRepository
import com.ricknout.rugbyranker.vo.RankingsType
import javax.inject.Inject

class MensRankingsViewModel @Inject constructor(rugbyRankerRepository: RugbyRankerRepository)
    : RankingsViewModel(RankingsType.MENS, rugbyRankerRepository)
