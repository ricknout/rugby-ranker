package com.ricknout.rugbyranker.teams.ui

import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.teams.repository.TeamsRepository
import com.ricknout.rugbyranker.teams.work.TeamsWorkManager
import javax.inject.Inject

class MensTeamsViewModel @Inject constructor(
    teamsRepository: TeamsRepository,
    teamsWorkManager: TeamsWorkManager
) : TeamsViewModel(Sport.MENS, teamsRepository, teamsWorkManager)
