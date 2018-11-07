package com.ricknout.rugbyranker.work

import android.content.Context
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.repository.RugbyRankerRepository
import com.ricknout.rugbyranker.vo.MatchStatus
import com.ricknout.rugbyranker.vo.Sport

class WomensCompleteWorldRugbyMatchesWorker(
        context: Context,
        workerParams: WorkerParameters,
        rugbyRankerRepository: RugbyRankerRepository
) : WorldRugbyMatchesWorker(context, workerParams, Sport.WOMENS, MatchStatus.COMPLETE, rugbyRankerRepository) {

    companion object {
        const val UNIQUE_WORK_NAME = "world_rugby_matches_worker_womens_complete"
    }
}
