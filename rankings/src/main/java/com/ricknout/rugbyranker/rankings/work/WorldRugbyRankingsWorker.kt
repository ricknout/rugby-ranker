package com.ricknout.rugbyranker.rankings.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.rankings.repository.RankingsRepository

open class WorldRugbyRankingsWorker(
        context: Context,
        workerParams: WorkerParameters,
        private val sport: Sport,
        private val rankingsRepository: RankingsRepository
) : Worker(context, workerParams) {

    override fun doWork() = fetchAndCacheLatestWorldRugbyRankings()

    private fun fetchAndCacheLatestWorldRugbyRankings(): Result {
        val success = rankingsRepository.fetchAndCacheLatestWorldRugbyRankingsSync(sport)
        return if (success) Result.success() else Result.retry()
    }
}
