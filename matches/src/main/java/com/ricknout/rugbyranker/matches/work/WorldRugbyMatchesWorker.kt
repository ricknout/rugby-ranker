package com.ricknout.rugbyranker.matches.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.matches.repository.MatchesRepository

open class WorldRugbyMatchesWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val sport: Sport,
    private val matchStatus: MatchStatus,
    private val matchesRepository: MatchesRepository
) : Worker(context, workerParams) {

    override fun doWork() = fetchAndCacheLatestWorldRugbyMatches()

    private fun fetchAndCacheLatestWorldRugbyMatches(): Result {
        val success = matchesRepository.fetchAndCacheLatestWorldRugbyMatchesSync(sport, matchStatus)
        return if (success) Result.success() else Result.retry()
    }
}
