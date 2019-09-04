package com.ricknout.rugbyranker.rankings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.ricknout.rugbyranker.core.util.DateUtils
import com.ricknout.rugbyranker.core.viewmodel.ScrollableViewModel
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.prediction.vo.Prediction
import com.ricknout.rugbyranker.rankings.prefs.RankingsSharedPreferences
import com.ricknout.rugbyranker.rankings.repository.RankingsRepository
import com.ricknout.rugbyranker.rankings.vo.RankingsCalculator
import com.ricknout.rugbyranker.rankings.vo.WorldRugbyRanking
import com.ricknout.rugbyranker.rankings.work.RankingsWorkManager

open class RankingsViewModel(
    private val sport: Sport,
    private val rankingsRepository: RankingsRepository,
    rankingsWorkManager: RankingsWorkManager
) : ScrollableViewModel() {

    init {
        rankingsWorkManager.fetchAndStoreLatestWorldRugbyRankings(sport)
    }

    val predictions = MutableLiveData<List<Prediction>>()

    private fun hasPredictions() = !(predictions.value?.isEmpty() ?: true)

    private val latestWorldRugbyRankings = rankingsRepository.loadLatestWorldRugbyRankings(sport)

    val latestWorldRugbyRankingsWorkInfos = Transformations.map(
        rankingsWorkManager.getLatestWorldRugbyRankingsWorkInfos(sport)
    ) { workInfos ->
        if (rankingsRepository.isInitialRankingsFetched(sport)) null else workInfos
    }

    private val _latestWorldRugbyRankingsEffectiveTime = MediatorLiveData<String>().apply {
        addSource(rankingsRepository.getLatestWorldRugbyRankingsEffectiveTimeMillisLiveData(sport)) { effectiveTimeMillis ->
            value = if (hasPredictions() || effectiveTimeMillis == RankingsSharedPreferences.DEFAULT_EFFECTIVE_TIME_MILLIS) {
                null
            } else {
                DateUtils.getDate(DateUtils.DATE_FORMAT_D_MMM_YYYY, effectiveTimeMillis)
            }
        }
        addSource(predictions) {
            val effectiveTimeMillis = rankingsRepository.getLatestWorldRugbyRankingsEffectiveTimeMillis(sport)
            value = if (hasPredictions() || effectiveTimeMillis == RankingsSharedPreferences.DEFAULT_EFFECTIVE_TIME_MILLIS) {
                null
            } else {
                DateUtils.getDate(DateUtils.DATE_FORMAT_D_MMM_YYYY, effectiveTimeMillis)
            }
        }
    }
    val latestWorldRugbyRankingsEffectiveTime: LiveData<String>
        get() = _latestWorldRugbyRankingsEffectiveTime

    private val _worldRugbyRankings = MediatorLiveData<List<WorldRugbyRanking>>().apply {
        addSource(latestWorldRugbyRankings) { latestWorldRugbyRankings ->
            if (!hasPredictions()) value = latestWorldRugbyRankings
        }
        addSource(predictions) { predictions ->
            val latestWorldRugbyRankings = latestWorldRugbyRankings.value ?: return@addSource
            if (predictions == null) return@addSource
            value = RankingsCalculator.allocatePointsForPredictions(
                    worldRugbyRankings = latestWorldRugbyRankings,
                    predictions = predictions
            )
        }
    }
    val worldRugbyRankings: LiveData<List<WorldRugbyRanking>>
        get() = _worldRugbyRankings

    private val _refreshingLatestWorldRugbyRankings = MutableLiveData<Boolean>().apply { value = false }
    val refreshingLatestWorldRugbyRankings: LiveData<Boolean>
        get() = _refreshingLatestWorldRugbyRankings

    fun refreshLatestWorldRugbyRankings(onComplete: (success: Boolean) -> Unit) {
        _refreshingLatestWorldRugbyRankings.value = true
        rankingsRepository.fetchAndCacheLatestWorldRugbyRankingsAsync(sport, viewModelScope) { success ->
            _refreshingLatestWorldRugbyRankings.value = false
            onComplete(success)
        }
    }
}
