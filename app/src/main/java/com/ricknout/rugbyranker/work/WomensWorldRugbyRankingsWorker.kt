package com.ricknout.rugbyranker.work

import android.content.Context
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.api.WorldRugbyRankingsService
import com.ricknout.rugbyranker.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.vo.RankingsType

class WomensWorldRugbyRankingsWorker(
        context: Context,
        workerParams: WorkerParameters,
        worldRugbyRankingsService: WorldRugbyRankingsService,
        worldRugbyRankingDao: WorldRugbyRankingDao
) : WorldRugbyRankingsWorker(context, workerParams, worldRugbyRankingsService, worldRugbyRankingDao, RankingsType.WOMENS) {

    companion object {
        const val UNIQUE_WORK_NAME = "world_rugby_rankings_worker_womens"
    }
}
