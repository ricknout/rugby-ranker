package dev.ricknout.rugbyranker.ranking.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.ricknout.rugbyranker.core.lifecycle.ScrollableViewModel
import dev.ricknout.rugbyranker.core.model.Ranking
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.prediction.model.Prediction
import dev.ricknout.rugbyranker.ranking.data.RankingDataStore
import dev.ricknout.rugbyranker.ranking.data.RankingRepository
import dev.ricknout.rugbyranker.ranking.util.RankingCalculator
import dev.ricknout.rugbyranker.ranking.work.RankingWorkManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

open class RankingViewModel(
    private val sport: Sport,
    private val repository: RankingRepository,
    workManager: RankingWorkManager,
) : ScrollableViewModel() {

    init {
        workManager.enqueueWork(sport)
    }

    val workInfos = workManager.getWorkInfos(sport)

    private val predictions = MutableStateFlow<List<Prediction>>(emptyList())

    private val updatedTimeMillis = repository.getUpdatedTimeMillis(sport)

    private val _rankings = repository.loadRankings(sport)
        .combine(updatedTimeMillis) { rankings, updatedTimeMillis ->
            rankings to updatedTimeMillis
        }
        .combine(predictions) { pair, predictions ->
            val rankings = RankingCalculator.allocatePointsForPredictions(pair.first, predictions)
            val updatedTimeMillis = when {
                pair.second == RankingDataStore.DEFAULT_UPDATED_TIME_MILLIS -> null
                rankings.isEmpty() || !predictions.isNullOrEmpty() -> null
                else -> pair.second
            }
            rankings to updatedTimeMillis
        }
    val rankings: LiveData<Pair<List<Ranking>, Long?>> = _rankings.asLiveData()

    private val _refreshingRankings = MutableStateFlow(false)
    val refreshingRankings: LiveData<Boolean> = _refreshingRankings.asLiveData()

    fun setPredictions(predictions: List<Prediction>) {
        this.predictions.value = predictions
    }

    fun refreshRankings(onComplete: (success: Boolean) -> Unit) {
        _refreshingRankings.value = true
        repository.fetchAndCacheLatestRankingsAsync(sport, viewModelScope) { success ->
            _refreshingRankings.value = false
            onComplete(success)
        }
    }
}
