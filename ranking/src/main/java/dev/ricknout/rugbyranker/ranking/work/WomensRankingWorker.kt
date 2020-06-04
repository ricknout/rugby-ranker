package dev.ricknout.rugbyranker.ranking.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.WorkerParameters
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.ranking.data.RankingRepository

class WomensRankingWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    repository: RankingRepository
) : RankingWorker(appContext, params, Sport.WOMENS, repository) {

    companion object {
        const val UNIQUE_WORK_NAME = "ranking_worker_womens"
    }
}
