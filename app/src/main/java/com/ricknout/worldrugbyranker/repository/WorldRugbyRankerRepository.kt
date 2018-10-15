package com.ricknout.worldrugbyranker.repository

import com.ricknout.worldrugbyranker.db.WorldRugbyRankingDao
import com.ricknout.worldrugbyranker.vo.RankingsType

class WorldRugbyRankerRepository(private val worldRugbyRankingDao: WorldRugbyRankingDao) {

    fun getLatestWorldRugbyRankings(rankingsType: RankingsType) = worldRugbyRankingDao.load(rankingsType)
}
