package com.ricknout.rugbyranker.work

import android.content.Context
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.repository.RankingsRepository

class MensWorldRugbyRankingsWorker(
        context: Context,
        workerParams: WorkerParameters,
        rankingsRepository: RankingsRepository
) : WorldRugbyRankingsWorker(context, workerParams, Sport.MENS, rankingsRepository) {

    companion object {
        const val UNIQUE_WORK_NAME = "world_rugby_rankings_worker_mens"
    }
}
