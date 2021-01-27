package dev.ricknout.rugbyranker.live.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.match.data.MatchRepository

@HiltWorker
class MensLiveMatchWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    repository: MatchRepository,
    workManager: WorkManager
) : LiveMatchWorker(appContext, params, Sport.MENS, repository, workManager) {

    companion object {
        fun getUniqueWorkName(matchId: Long) = "live_match_worker_mens_$matchId"
    }
}
