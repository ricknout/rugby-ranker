package dev.ricknout.rugbyranker.ranking.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.ranking.data.RankingRepository

@HiltWorker
class MensRankingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    repository: RankingRepository,
) : RankingWorker(appContext, params, Sport.MENS, repository) {

    companion object {
        const val UNIQUE_WORK_NAME = "ranking_worker_mens"
    }
}
