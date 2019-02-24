package com.ricknout.rugbyranker.matches.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.matches.repository.MatchesRepository

open class WorldRugbyMatchesWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val sport: Sport,
    private val matchStatus: MatchStatus,
    private val matchesRepository: MatchesRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork() = fetchAndCacheLatestWorldRugbyMatches()

    private suspend fun fetchAndCacheLatestWorldRugbyMatches(): Result {
        val result = matchesRepository.fetchAndCacheLatestWorldRugbyMatchesSync(sport, matchStatus)
        val success = result.first
        return if (success) Result.success() else Result.retry()
    }
}
