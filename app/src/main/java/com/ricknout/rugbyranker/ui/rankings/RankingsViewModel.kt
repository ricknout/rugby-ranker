package com.ricknout.rugbyranker.ui.rankings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ricknout.rugbyranker.common.util.DateUtils
import com.ricknout.rugbyranker.prefs.RugbyRankerSharedPreferences
import com.ricknout.rugbyranker.repository.RugbyRankerRepository
import com.ricknout.rugbyranker.vo.MatchPrediction
import com.ricknout.rugbyranker.vo.RankingsCalculator
import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.vo.WorldRugbyRanking
import com.ricknout.rugbyranker.work.RugbyRankerWorkManager

open class RankingsViewModel(
        private val sport: Sport,
        private val rugbyRankerRepository: RugbyRankerRepository,
        rugbyRankerWorkManager: RugbyRankerWorkManager
) : ViewModel() {

    init {
        rugbyRankerWorkManager.fetchAndStoreLatestWorldRugbyRankings(sport)
    }

    private val _editingMatchPrediction = MutableLiveData<MatchPrediction>().apply { value = null }
    val editingMatchPrediction: LiveData<MatchPrediction>
        get() = _editingMatchPrediction

    private val _matchPredictions = MediatorLiveData<List<MatchPrediction>>().apply {
        addSource(_editingMatchPrediction) { editingMatchPrediction ->
            val currentMatchPredictions = value?.map { matchPrediction ->
                val isEditing = editingMatchPrediction != null && matchPrediction.id == editingMatchPrediction.id
                matchPrediction.copy(isEditing = isEditing)
            }
            value = currentMatchPredictions
        }
        value = null
    }
    val matchPredictions: LiveData<List<MatchPrediction>>
        get() = _matchPredictions

    val latestWorldRugbyRankings = rugbyRankerRepository.loadLatestWorldRugbyRankings(sport)
    val latestWorldRugbyRankingsWorkInfos = rugbyRankerWorkManager.getLatestWorldRugbyRankingsWorkInfos(sport)

    private val _latestWorldRugbyRankingsEffectiveTime = MediatorLiveData<String>().apply {
        addSource(rugbyRankerRepository.getLatestWorldRugbyRankingsEffectiveTimeMillisLiveData(sport)) { effectiveTimeMillis ->
            value = if (hasMatchPredictions() || effectiveTimeMillis == RugbyRankerSharedPreferences.DEFAULT_EFFECTIVE_TIME_MILLIS) {
                null
            } else {
                DateUtils.getDate(DateUtils.DATE_FORMAT_D_MMM_YYYY, effectiveTimeMillis)
            }
        }
        addSource(_matchPredictions) {
            val effectiveTimeMillis = rugbyRankerRepository.getLatestWorldRugbyRankingsEffectiveTimeMillis(sport)
            value = if (hasMatchPredictions() || effectiveTimeMillis == RugbyRankerSharedPreferences.DEFAULT_EFFECTIVE_TIME_MILLIS) {
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
            if (!hasMatchPredictions()) value = latestWorldRugbyRankings
        }
        addSource(_matchPredictions) { matchPredictions ->
            val latestWorldRugbyRankings = latestWorldRugbyRankings.value ?: return@addSource
            if (matchPredictions == null) return@addSource
            value = RankingsCalculator.allocatePointsForMatchPredictions(
                    worldRugbyRankings = latestWorldRugbyRankings,
                    matchPredictions = matchPredictions
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
        rugbyRankerRepository.fetchAndCacheLatestWorldRugbyRankingsAsync(sport) { success ->
            _refreshingLatestWorldRugbyRankings.value = false
            onComplete(success)
        }
    }

    fun hasMatchPredictions() = !(_matchPredictions.value?.isEmpty() ?: true)

    fun getMatchPredictionCount() = _matchPredictions.value?.size ?: 0

    fun isEditingMatchPrediction() = _editingMatchPrediction.value != null

    fun addMatchPrediction(matchPrediction: MatchPrediction) {
        val currentMatchPredictions = (_matchPredictions.value ?: emptyList()).toMutableList()
        currentMatchPredictions.add(matchPrediction)
        _matchPredictions.value = currentMatchPredictions
    }

    fun beginEditMatchPrediction(matchPrediction: MatchPrediction) {
        if (_editingMatchPrediction.value == matchPrediction) return
        _editingMatchPrediction.value = matchPrediction
    }

    fun endEditMatchPrediction() {
        if (_editingMatchPrediction.value == null) return
        _editingMatchPrediction.value = null
    }

    fun editMatchPrediction(matchPrediction: MatchPrediction) {
        val currentMatchPredictions = _matchPredictions.value!!.map { currentMatchPrediction ->
            if (currentMatchPrediction.id == matchPrediction.id) {
                matchPrediction
            } else {
                currentMatchPrediction
            }
        }
        _matchPredictions.value = currentMatchPredictions
    }

    fun removeMatchPrediction(matchPrediction: MatchPrediction): Boolean {
        val removedEditingMatchPrediction = _editingMatchPrediction.value?.id == matchPrediction.id
        val currentMatchPredictions = _matchPredictions.value!!.toMutableList()
        currentMatchPredictions.remove(matchPrediction)
        if (currentMatchPredictions.isEmpty()) {
            reset()
            return removedEditingMatchPrediction
        }
        _matchPredictions.value = currentMatchPredictions
        if (removedEditingMatchPrediction) _editingMatchPrediction.value = null
        return removedEditingMatchPrediction
    }

    private fun reset() {
        _matchPredictions.value = null
        _editingMatchPrediction.value = null
        _worldRugbyRankings.value = latestWorldRugbyRankings.value
    }

    val homeTeamInputValid = MutableLiveData<Boolean>()
    val homePointsInputValid = MutableLiveData<Boolean>()
    val awayTeamInputValid = MutableLiveData<Boolean>()
    val awayPointsInputValid = MutableLiveData<Boolean>()

    private val _matchPredictionInputValid = MediatorLiveData<Boolean>().apply {
        addSource(homeTeamInputValid) { value = isMatchPredictionInputValid() }
        addSource(homePointsInputValid) { value = isMatchPredictionInputValid() }
        addSource(awayTeamInputValid) { value = isMatchPredictionInputValid() }
        addSource(awayPointsInputValid) { value = isMatchPredictionInputValid() }
        value = false
    }
    val matchPredictionInputValid: LiveData<Boolean>
        get() = _matchPredictionInputValid

    fun resetMatchPredictionInputValid() {
        homeTeamInputValid.value = false
        homePointsInputValid.value = false
        awayTeamInputValid.value = false
        awayPointsInputValid.value = false
    }

    private fun isMatchPredictionInputValid() = homeTeamInputValid.value == true && homePointsInputValid.value == true
            && awayTeamInputValid.value == true && awayPointsInputValid.value == true

    val showMatchPredictionInput = MutableLiveData<Boolean>().apply { value = true }

    private val _matchPredictionInputState = MediatorLiveData<MatchPredictionInputState>().apply {
        addSource(showMatchPredictionInput) { showMatchPredictionInput ->
            value = value?.copy(showMatchPredictionInput = showMatchPredictionInput)
        }
        addSource(_matchPredictions) {
            value = value?.copy(hasMatchPredictions = hasMatchPredictions())
        }
        value = MatchPredictionInputState()
    }
    val matchPredictionInputState: LiveData<MatchPredictionInputState>
        get() = _matchPredictionInputState

    data class MatchPredictionInputState(val showMatchPredictionInput: Boolean = true, val hasMatchPredictions: Boolean = false)
}
