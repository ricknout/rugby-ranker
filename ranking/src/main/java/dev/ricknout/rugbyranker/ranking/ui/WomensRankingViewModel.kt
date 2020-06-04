package dev.ricknout.rugbyranker.ranking.ui

import androidx.hilt.lifecycle.ViewModelInject
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.ranking.data.RankingRepository
import dev.ricknout.rugbyranker.ranking.work.RankingWorkManager

class WomensRankingViewModel @ViewModelInject constructor(
    repository: RankingRepository,
    workManager: RankingWorkManager
) : RankingViewModel(Sport.WOMENS, repository, workManager)
