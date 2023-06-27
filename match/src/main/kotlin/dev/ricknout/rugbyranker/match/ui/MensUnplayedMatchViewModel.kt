package dev.ricknout.rugbyranker.match.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.match.data.MatchRepository
import dev.ricknout.rugbyranker.match.model.Status
import javax.inject.Inject

@HiltViewModel
class MensUnplayedMatchViewModel @Inject constructor(
    repository: MatchRepository,
) : MatchViewModel(Sport.MENS, Status.UNPLAYED, repository)
