package com.ricknout.rugbyranker.work

import android.content.Context
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.repository.RugbyRankerRepository
import com.ricknout.rugbyranker.vo.MatchStatus
import com.ricknout.rugbyranker.common.vo.Sport

class WomensUnplayedWorldRugbyMatchesWorker(
        context: Context,
        workerParams: WorkerParameters,
        rugbyRankerRepository: RugbyRankerRepository
) : WorldRugbyMatchesWorker(context, workerParams, Sport.WOMENS, MatchStatus.UNPLAYED, rugbyRankerRepository) {

    companion object {
        const val UNIQUE_WORK_NAME = "world_rugby_matches_worker_womens_unplayed"
    }
}
