package dev.ricknout.rugbyranker.ranking.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.ranking.data.RankingRepository
import dev.ricknout.rugbyranker.ranking.work.RankingWorkManager
import javax.inject.Inject

@HiltViewModel
class MensRankingViewModel @Inject constructor(
    repository: RankingRepository,
    workManager: RankingWorkManager
) : RankingViewModel(Sport.MENS, repository, workManager)
