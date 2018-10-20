package com.ricknout.rugbyranker.ui.rankings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ricknout.rugbyranker.repository.RugbyRankerRepository
import com.ricknout.rugbyranker.vo.MatchResult
import com.ricknout.rugbyranker.vo.RankingsCalculator
import com.ricknout.rugbyranker.vo.RankingsType
import com.ricknout.rugbyranker.vo.WorldRugbyRanking
import com.ricknout.rugbyranker.work.RugbyRankerWorkManager

open class RankingsViewModel(
        rankingsType: RankingsType,
        rugbyRankerRepository: RugbyRankerRepository,
        rugbyRankerWorkManager: RugbyRankerWorkManager
) : ViewModel() {

    init {
        rugbyRankerWorkManager.fetchAndStoreLatestWorldRugbyRankings(rankingsType)
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

    val latestWorldRugbyRankings = rugbyRankerRepository.loadLatestWorldRugbyRankings(rankingsType)
    val latestWorldRugbyRankingsStatuses = rugbyRankerWorkManager.getLatestWorldRugbyRankingsStatuses(rankingsType)

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

    fun hasMatchResults() = !(_matchResults.value?.isEmpty() ?: true)

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
        _addOrEditMatchInputValid.value = false
    }

    private fun isAddOrEditMatchInputValid() = homeTeamInputValid.value == true && homePointsInputValid.value == true
            && awayTeamInputValid.value == true && awayPointsInputValid.value == true
}
