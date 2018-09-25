package com.ricknout.worldrugbyranker.ui.ranking

import androidx.lifecycle.ViewModel
import com.ricknout.worldrugbyranker.repository.WorldRugbyRankerRepository
import javax.inject.Inject

class RankingsViewModel @Inject constructor(private val worldRugbyRankerRepository: WorldRugbyRankerRepository) : ViewModel() {

    val mensWorldRugbyRankings = worldRugbyRankerRepository.getLatestMensWorldRugbyRankings()

    val womensWorldRugbyRankings = worldRugbyRankerRepository.getLatestWomensWorldRugbyRankings()
}