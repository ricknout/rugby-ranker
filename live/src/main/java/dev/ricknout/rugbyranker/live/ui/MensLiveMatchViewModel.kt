package dev.ricknout.rugbyranker.live.ui

import androidx.hilt.lifecycle.ViewModelInject
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.match.data.MatchRepository

class MensLiveMatchViewModel @ViewModelInject constructor(
    repository: MatchRepository
) : LiveMatchViewModel(Sport.MENS, repository)
