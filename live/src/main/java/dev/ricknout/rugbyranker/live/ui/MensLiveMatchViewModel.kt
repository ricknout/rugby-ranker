package dev.ricknout.rugbyranker.live.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.live.work.LiveMatchWorkManager
import dev.ricknout.rugbyranker.match.data.MatchRepository
import javax.inject.Inject

@HiltViewModel
class MensLiveMatchViewModel @Inject constructor(
    repository: MatchRepository,
    workManager: LiveMatchWorkManager,
) : LiveMatchViewModel(Sport.MENS, repository, workManager)
