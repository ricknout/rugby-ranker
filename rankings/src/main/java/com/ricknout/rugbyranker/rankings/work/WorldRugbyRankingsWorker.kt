package com.ricknout.rugbyranker.rankings.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.rankings.repository.RankingsRepository

open class WorldRugbyRankingsWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val sport: Sport,
    private val rankingsRepository: RankingsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork() = fetchAndCacheLatestWorldRugbyRankings()

    private suspend fun fetchAndCacheLatestWorldRugbyRankings(): Result {
        val success = rankingsRepository.fetchAndCacheLatestWorldRugbyRankingsSync(sport)
        return if (success) Result.success() else Result.retry()
    }
}
