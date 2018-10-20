package com.ricknout.rugbyranker.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.api.WorldRugbyService
import com.ricknout.rugbyranker.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.common.util.DateUtils
import com.ricknout.rugbyranker.vo.RankingsType
import com.ricknout.rugbyranker.vo.WorldRugbyRankingDataConverter

open class WorldRugbyRankingsWorker(
        context: Context,
        workerParams: WorkerParameters,
        private val worldRugbyService: WorldRugbyService,
        private val worldRugbyRankingDao: WorldRugbyRankingDao,
        private val rankingsType: RankingsType
) : Worker(context, workerParams) {

    override fun doWork() = fetchAndCacheRankings()

    private fun fetchAndCacheRankings(): Result {
        val json = when (rankingsType) {
            RankingsType.MENS -> WorldRugbyService.JSON_MENS
            RankingsType.WOMENS -> WorldRugbyService.JSON_WOMENS
        }
        val date = getCurrentDate()
        val response = worldRugbyService.getRankings(json, date).execute()
        if (response.isSuccessful) {
            val worldRugbyRankingsResponse = response.body() ?: return Result.RETRY
            val worldRugbyRankings = WorldRugbyRankingDataConverter.convertFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse, rankingsType)
            worldRugbyRankingDao.insert(worldRugbyRankings)
            return Result.SUCCESS
        }
        return Result.RETRY
    }

    private fun getCurrentDate() = DateUtils.getCurrentDate(WorldRugbyService.DATE_FORMAT)
}
