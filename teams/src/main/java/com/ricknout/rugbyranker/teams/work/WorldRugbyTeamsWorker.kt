package com.ricknout.rugbyranker.teams.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.teams.repository.TeamsRepository

open class WorldRugbyTeamsWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val sport: Sport,
    private val teamsRepository: TeamsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork() = fetchAndCacheLatestWorldRugbyTeams()

    private suspend fun fetchAndCacheLatestWorldRugbyTeams(): Result {
        val success = teamsRepository.fetchAndCacheLatestWorldRugbyTeamsSync(sport)
        return if (success) Result.success() else Result.retry()
    }
}
