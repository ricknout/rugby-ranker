package dev.ricknout.rugbyranker.match.ui

import androidx.hilt.lifecycle.ViewModelInject
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.match.data.MatchRepository
import dev.ricknout.rugbyranker.match.model.Status

class WomensUnplayedMatchViewModel @ViewModelInject constructor(
    repository: MatchRepository
) : MatchViewModel(Sport.WOMENS, Status.UNPLAYED, repository)
