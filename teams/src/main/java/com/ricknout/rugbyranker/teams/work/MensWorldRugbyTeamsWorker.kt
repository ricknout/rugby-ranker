package com.ricknout.rugbyranker.teams.work

import android.content.Context
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.teams.repository.TeamsRepository
import javax.inject.Inject

class MensWorldRugbyTeamsWorker @Inject constructor(
    context: Context,
    workerParams: WorkerParameters,
    teamsRepository: TeamsRepository
) : WorldRugbyTeamsWorker(context, workerParams, Sport.MENS, teamsRepository) {

    companion object {
        const val UNIQUE_WORK_NAME = "world_rugby_teams_worker_mens"
    }
}
