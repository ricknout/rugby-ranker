package com.ricknout.rugbyranker.ui.rankings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ricknout.rugbyranker.common.util.DateUtils
import com.ricknout.rugbyranker.prefs.RugbyRankerSharedPreferences
import com.ricknout.rugbyranker.repository.RugbyRankerRepository
import com.ricknout.rugbyranker.vo.MatchResult
import com.ricknout.rugbyranker.vo.RankingsCalculator
import com.ricknout.rugbyranker.vo.Sport
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

    private val _editingMatchResult = MutableLiveData<MatchResult>().apply { value = null }
    val editingMatchResult: LiveData<MatchResult>
        get() = _editingMatchResult

    private val _matchResults = MediatorLiveData<List<MatchResult>>().apply {
        addSource(_editingMatchResult) { editingMatchResult ->
            val currentMatchResults = value?.map { matchResult ->
                val isEditing = editingMatchResult != null && matchResult.id == editingMatchResult.id
                matchResult.copy(isEditing = isEditing)
            }
            value = currentMatchResults
        }
        value = null
    }
    val matchResults: LiveData<List<MatchResult>>
        get() = _matchResults

    val latestWorldRugbyRankings = rugbyRankerRepository.loadLatestWorldRugbyRankings(sport)
    val latestWorldRugbyRankingsWorkInfos = rugbyRankerWorkManager.getLatestWorldRugbyRankingsWorkInfos(sport)

    private val _latestWorldRugbyRankingsEffectiveTime = MediatorLiveData<String>().apply {
        addSource(rugbyRankerRepository.getLatestWorldRugbyRankingsEffectiveTimeMillisLiveData(sport)) { effectiveTimeMillis ->
            value = if (hasMatchResults() || effectiveTimeMillis == RugbyRankerSharedPreferences.DEFAULT_EFFECTIVE_TIME_MILLIS) {
                null
            } else {
                DateUtils.getDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, effectiveTimeMillis)
            }
        }
        addSource(_matchResults) { _ ->
            val effectiveTimeMillis = rugbyRankerRepository.getLatestWorldRugbyRankingsEffectiveTimeMillis(sport)
            value = if (hasMatchResults() || effectiveTimeMillis == RugbyRankerSharedPreferences.DEFAULT_EFFECTIVE_TIME_MILLIS) {
                null
            } else {
                DateUtils.getDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, effectiveTimeMillis)
            }
        }
    }
    val latestWorldRugbyRankingsEffectiveTime: LiveData<String>
        get() = _latestWorldRugbyRankingsEffectiveTime

    private val _worldRugbyRankings = MediatorLiveData<List<WorldRugbyRanking>>().apply {
        addSource(latestWorldRugbyRankings) { latestWorldRugbyRankings ->
            if (!hasMatchResults()) value = latestWorldRugbyRankings
        }
        addSource(_matchResults) { matchResults ->
            val latestWorldRugbyRankings = latestWorldRugbyRankings.value ?: return@addSource
            if (matchResults == null) return@addSource
            value = RankingsCalculator.allocatePointsForMatchResults(
                    worldRugbyRankings = latestWorldRugbyRankings,
                    matchResults = matchResults
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

    fun hasMatchResults() = !(_matchResults.value?.isEmpty() ?: true)

    fun getMatchResultCount() = _matchResults.value?.size ?: 0

    fun isEditingMatchResult() = _editingMatchResult.value != null

    fun addMatchResult(matchResult: MatchResult) {
        val currentMatchResults = (_matchResults.value ?: emptyList()).toMutableList()
        currentMatchResults.add(matchResult)
        _matchResults.value = currentMatchResults
    }

    fun beginEditMatchResult(matchResult: MatchResult) {
        if (_editingMatchResult.value == matchResult) return
        _editingMatchResult.value = matchResult
    }

    fun endEditMatchResult() {
        if (_editingMatchResult.value == null) return
        _editingMatchResult.value = null
    }

    fun editMatchResult(matchResult: MatchResult) {
        val currentMatchResults = _matchResults.value!!.map { currentMatchResult ->
            if (currentMatchResult.id == matchResult.id) {
                matchResult
            } else {
                currentMatchResult
            }
        }
        _matchResults.value = currentMatchResults
    }

    fun removeMatchResult(matchResult: MatchResult): Boolean {
        val removedEditingMatchResult = _editingMatchResult.value?.id == matchResult.id
        val currentMatchResults = _matchResults.value!!.toMutableList()
        currentMatchResults.remove(matchResult)
        if (currentMatchResults.isEmpty()) {
            reset()
            return removedEditingMatchResult
        }
        _matchResults.value = currentMatchResults
        if (removedEditingMatchResult) _editingMatchResult.value = null
        return removedEditingMatchResult
    }

    private fun reset() {
        _matchResults.value = null
        _editingMatchResult.value = null
        _worldRugbyRankings.value = latestWorldRugbyRankings.value
    }

    val homeTeamInputValid = MutableLiveData<Boolean>()
    val homePointsInputValid = MutableLiveData<Boolean>()
    val awayTeamInputValid = MutableLiveData<Boolean>()
    val awayPointsInputValid = MutableLiveData<Boolean>()

    private val _addOrEditMatchInputValid = MediatorLiveData<Boolean>().apply {
        addSource(homeTeamInputValid) { value = isAddOrEditMatchInputValid() }
        addSource(homePointsInputValid) { value = isAddOrEditMatchInputValid() }
        addSource(awayTeamInputValid) { value = isAddOrEditMatchInputValid() }
        addSource(awayPointsInputValid) { value = isAddOrEditMatchInputValid() }
        value = false
    }
    val addOrEditMatchInputValid: LiveData<Boolean>
        get() = _addOrEditMatchInputValid

    fun resetAddOrEditMatchInputValid() {
        homeTeamInputValid.value = false
        homePointsInputValid.value = false
        awayTeamInputValid.value = false
        awayPointsInputValid.value = false
    }

    private fun isAddOrEditMatchInputValid() = homeTeamInputValid.value == true && homePointsInputValid.value == true
            && awayTeamInputValid.value == true && awayPointsInputValid.value == true
}
