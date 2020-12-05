package dev.ricknout.rugbyranker.live.ui

import androidx.hilt.lifecycle.ViewModelInject
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.live.work.LiveMatchWorkManager
import dev.ricknout.rugbyranker.match.data.MatchRepository

class WomensLiveMatchViewModel @ViewModelInject constructor(
    repository: MatchRepository,
    workManager: LiveMatchWorkManager
) : LiveMatchViewModel(Sport.WOMENS, repository, workManager)
