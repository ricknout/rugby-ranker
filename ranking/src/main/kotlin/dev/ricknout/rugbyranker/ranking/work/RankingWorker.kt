package dev.ricknout.rugbyranker.ranking.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.ranking.data.RankingRepository

open class RankingWorker(
    appContext: Context,
    params: WorkerParameters,
    private val sport: Sport,
    private val repository: RankingRepository,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork() = fetchAndCacheLatestRankings()

    private suspend fun fetchAndCacheLatestRankings(): Result {
        val success = repository.fetchAndCacheLatestRankingsSync(sport)
        return if (success) Result.success() else Result.retry()
    }
}
