package com.ricknout.worldrugbyranker.repository

import androidx.lifecycle.LiveData
import com.ricknout.worldrugbyranker.db.WorldRugbyRankingDao
import com.ricknout.worldrugbyranker.vo.RankingsType
import com.ricknout.worldrugbyranker.vo.WorldRugbyRanking
import com.ricknout.worldrugbyranker.work.WorldRugbyRankerWorkManager

class WorldRugbyRankerRepository(
        private val worldRugbyRankerWorkManager: WorldRugbyRankerWorkManager,
        private val worldRugbyRankingDao: WorldRugbyRankingDao
) {

    fun getLatestWorldRugbyRankings(rankingsType: RankingsType): LiveData<List<WorldRugbyRanking>> {
        worldRugbyRankerWorkManager.fetchAndStoreWorldRugbyRankings()
        return worldRugbyRankingDao.load(rankingsType)
    }

    fun getLatestWorldRugbyRankingsStatuses(rankingsType: RankingsType) = worldRugbyRankerWorkManager.getWorldRugbyRankingsStatuses(rankingsType)
}
