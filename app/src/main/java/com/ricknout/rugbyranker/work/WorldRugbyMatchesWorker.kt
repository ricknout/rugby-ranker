package com.ricknout.rugbyranker.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.Result
import com.ricknout.rugbyranker.repository.RugbyRankerRepository
import com.ricknout.rugbyranker.vo.MatchStatus
import com.ricknout.rugbyranker.vo.Sport

open class WorldRugbyMatchesWorker(
        context: Context,
        workerParams: WorkerParameters,
        private val sport: Sport,
        private val matchStatus: MatchStatus,
        private val rugbyRankerRepository: RugbyRankerRepository
) : Worker(context, workerParams) {

    override fun doWork() = fetchAndCacheLatestWorldRugbyMatches()

    private fun fetchAndCacheLatestWorldRugbyMatches(): Result {
        val success = rugbyRankerRepository.fetchAndCacheLatestWorldRugbyMatchesSync(sport, matchStatus)
        return if (success) Result.success() else Result.retry()
    }
}
