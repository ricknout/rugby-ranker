package dev.ricknout.rugbyranker.ranking.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.WorkerParameters
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.ranking.data.RankingRepository

class MensRankingWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    repository: RankingRepository
) : RankingWorker(appContext, params, Sport.MENS, repository) {

    companion object {
        const val UNIQUE_WORK_NAME = "ranking_worker_mens"
    }
}
