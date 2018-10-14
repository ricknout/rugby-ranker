package com.ricknout.worldrugbyranker.ui.rankings

import com.ricknout.worldrugbyranker.repository.WorldRugbyRankerRepository
import com.ricknout.worldrugbyranker.vo.RankingsType
import javax.inject.Inject

class WomensRankingsViewModel @Inject constructor(worldRugbyRankerRepository: WorldRugbyRankerRepository)
    : RankingsViewModel(RankingsType.WOMENS, worldRugbyRankerRepository)
