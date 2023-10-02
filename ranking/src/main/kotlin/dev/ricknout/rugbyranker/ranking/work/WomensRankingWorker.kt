package dev.ricknout.rugbyranker.ranking.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.ranking.data.RankingRepository

@HiltWorker
class WomensRankingWorker
    @AssistedInject
    constructor(
        @Assisted appContext: Context,
        @Assisted params: WorkerParameters,
        repository: RankingRepository,
    ) : RankingWorker(appContext, params, Sport.WOMENS, repository) {
        companion object {
            const val UNIQUE_WORK_NAME = "ranking_worker_womens"
        }
    }
