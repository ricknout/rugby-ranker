package com.ricknout.rugbyranker.repository

import androidx.lifecycle.LiveData
import com.ricknout.rugbyranker.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.vo.RankingsType
import com.ricknout.rugbyranker.vo.WorldRugbyRanking
import com.ricknout.rugbyranker.work.RugbyRankerWorkManager

class RugbyRankerRepository(
        private val rugbyRankerWorkManager: RugbyRankerWorkManager,
        private val worldRugbyRankingDao: WorldRugbyRankingDao
) {

    fun getLatestWorldRugbyRankings(rankingsType: RankingsType): LiveData<List<WorldRugbyRanking>> {
        rugbyRankerWorkManager.fetchAndStoreWorldRugbyRankings()
        return worldRugbyRankingDao.load(rankingsType)
    }

    fun getLatestWorldRugbyRankingsStatuses(rankingsType: RankingsType) = rugbyRankerWorkManager.getWorldRugbyRankingsStatuses(rankingsType)
}
