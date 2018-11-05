package com.ricknout.rugbyranker.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.repository.RugbyRankerRepository
import com.ricknout.rugbyranker.vo.Sport

open class WorldRugbyRankingsWorker(
        context: Context,
        workerParams: WorkerParameters,
        private val sport: Sport,
        private val rugbyRankerRepository: RugbyRankerRepository
) : Worker(context, workerParams) {

    override fun doWork() = fetchAndCacheLatestWorldRugbyRankings()

    private fun fetchAndCacheLatestWorldRugbyRankings(): Result {
        val success = rugbyRankerRepository.fetchAndCacheLatestWorldRugbyRankingsSync(sport)
        return if (success) Result.SUCCESS else Result.RETRY
    }
}
