package com.ricknout.rugbyranker.work

import android.content.Context
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.repository.RugbyRankerRepository
import com.ricknout.rugbyranker.common.vo.Sport

class MensWorldRugbyRankingsWorker(
        context: Context,
        workerParams: WorkerParameters,
        rugbyRankerRepository: RugbyRankerRepository
) : WorldRugbyRankingsWorker(context, workerParams, Sport.MENS, rugbyRankerRepository) {

    companion object {
        const val UNIQUE_WORK_NAME = "world_rugby_rankings_worker_mens"
    }
}
