package com.ricknout.rugbyranker.teams.ui

import androidx.lifecycle.ViewModel
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.teams.repository.TeamsRepository
import com.ricknout.rugbyranker.teams.work.TeamsWorkManager

open class TeamsViewModel(
    sport: Sport,
    teamsRepository: TeamsRepository,
    teamsWorkManager: TeamsWorkManager
) : ViewModel() {

    init {
        teamsWorkManager.fetchAndStoreLatestWorldRugbyTeams(sport)
    }

    val latestWorldRugbyTeams = teamsRepository.loadLatestWorldRugbyTeams(sport)

    fun getLatestWorldRugbyTeam(position: Int) = latestWorldRugbyTeams.value?.get(position)
}
