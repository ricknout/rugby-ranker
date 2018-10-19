package com.ricknout.rugbyranker.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.api.WorldRugbyRankingsService
import com.ricknout.rugbyranker.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.util.DateUtils
import com.ricknout.rugbyranker.vo.RankingsType
import com.ricknout.rugbyranker.vo.WorldRugbyRankingDataConverter
import javax.inject.Inject

abstract class WorldRugbyRankingsWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    @Inject
    lateinit var worldRugbyRankingsService: WorldRugbyRankingsService

    @Inject
    lateinit var worldRugbyRankingDao: WorldRugbyRankingDao

    abstract fun getRankingsType(): RankingsType

    override fun doWork() = fetchAndCacheRankings()

    private fun fetchAndCacheRankings(): Result {
        val rankingsType = getRankingsType()
        val json = when (rankingsType) {
            RankingsType.MENS -> WorldRugbyRankingsService.JSON_MENS
            RankingsType.WOMENS -> WorldRugbyRankingsService.JSON_WOMENS
        }
        val date = getCurrentDate()
        val response = worldRugbyRankingsService.getWorldRugbyRankings(json, date).execute()
        if (response.isSuccessful) {
            val worldRugbyRankingsResponse = response.body() ?: return Result.RETRY
            val worldRugbyRankings = WorldRugbyRankingDataConverter.convertFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse, rankingsType)
            worldRugbyRankingDao.insert(worldRugbyRankings)
            return Result.SUCCESS
        }
        return Result.RETRY
    }

    private fun getCurrentDate(): String = DateUtils.getCurrentDate(DATE_FORMAT)

    companion object {
        private const val DATE_FORMAT = "yyyy-MM-dd"
    }
}
