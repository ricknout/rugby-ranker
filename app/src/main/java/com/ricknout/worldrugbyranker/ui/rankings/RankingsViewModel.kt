package com.ricknout.worldrugbyranker.ui.rankings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ricknout.worldrugbyranker.repository.WorldRugbyRankerRepository
import com.ricknout.worldrugbyranker.vo.MatchResult
import com.ricknout.worldrugbyranker.vo.RankingsCalculator
import com.ricknout.worldrugbyranker.vo.WorldRugbyRanking
import javax.inject.Inject

class RankingsViewModel @Inject constructor(worldRugbyRankerRepository: WorldRugbyRankerRepository) : ViewModel() {

    // Mens

    private val _mensEditingMatchResult = MutableLiveData<MatchResult>().apply { value = null }
    val mensEditingMatchResult: LiveData<MatchResult>
        get() = _mensEditingMatchResult

    private val _mensMatches = MediatorLiveData<List<MatchResult>>().apply {
        addSource(_mensEditingMatchResult) { mensEditingMatchResult ->
            val currentMensMatches = value?.map { mensMatchResult ->
                val isEditing = mensEditingMatchResult != null && mensMatchResult.id == mensEditingMatchResult.id
                mensMatchResult.copy(isEditing = isEditing)
            }
            value = currentMensMatches
        }
        value = null
    }
    val mensMatches: LiveData<List<MatchResult>>
        get() = _mensMatches

    val latestMensWorldRugbyRankings = worldRugbyRankerRepository.getLatestMensWorldRugbyRankings()
    private val _mensWorldRugbyRankings = MediatorLiveData<List<WorldRugbyRanking>>().apply {
        addSource(latestMensWorldRugbyRankings) { mensWorldRugbyRankings ->
            if (!hasMensMatches()) value = mensWorldRugbyRankings
        }
        addSource(_mensMatches) { mensMatches ->
            val latestMensWorldRugbyRankings = latestMensWorldRugbyRankings.value ?: return@addSource
            if (mensMatches == null) return@addSource
            value = RankingsCalculator.allocatePointsForMatchResults(
                    worldRugbyRankings = latestMensWorldRugbyRankings,
                    matchResults = mensMatches
            )
        }
    }
    val mensWorldRugbyRankings: LiveData<List<WorldRugbyRanking>>
        get() = _mensWorldRugbyRankings

    fun hasMensMatches() = !(_mensMatches.value?.isEmpty() ?: true)

    fun isEditingMensMatch() = _mensEditingMatchResult.value != null

    fun addMensMatchResult(matchResult: MatchResult) {
        val currentMensMatches = (_mensMatches.value ?: emptyList()).toMutableList()
        currentMensMatches.add(matchResult)
        _mensMatches.value = currentMensMatches
    }

    fun beginEditMensMatchResult(matchResult: MatchResult) {
        if (_mensEditingMatchResult.value == matchResult) return
        _mensEditingMatchResult.value = matchResult
    }

    fun endEditMensMatchResult() {
        if (_mensEditingMatchResult.value == null) return
        _mensEditingMatchResult.value = null
    }

    fun editMensMatchResult(matchResult: MatchResult) {
        val currentMensMatches = _mensMatches.value!!.map { mensMatchResult ->
            if (mensMatchResult.id == matchResult.id) {
                matchResult
            } else {
                mensMatchResult
            }
        }
        _mensMatches.value = currentMensMatches
    }

    fun removeMensMatchResult(matchResult: MatchResult): Boolean {
        val removedMensEditingMatchResult = _mensEditingMatchResult.value?.id == matchResult.id
        val currentMensMatches = _mensMatches.value!!.toMutableList()
        currentMensMatches.remove(matchResult)
        if (currentMensMatches.isEmpty()) {
            resetMens()
            return removedMensEditingMatchResult
        }
        _mensMatches.value = currentMensMatches
        if (removedMensEditingMatchResult) _mensEditingMatchResult.value = null
        return removedMensEditingMatchResult
    }

    private fun resetMens() {
        _mensMatches.value = null
        _mensEditingMatchResult.value = null
        _mensWorldRugbyRankings.value = latestMensWorldRugbyRankings.value
    }

    val mensHomeTeamInputValid = MutableLiveData<Boolean>()
    val mensHomePointsInputValid = MutableLiveData<Boolean>()
    val mensAwayTeamInputValid = MutableLiveData<Boolean>()
    val mensAwayPointsInputValid = MutableLiveData<Boolean>()

    private val _mensAddOrEditMatchInputValid = MediatorLiveData<Boolean>().apply {
        addSource(mensHomeTeamInputValid) { value = isMensAddOrEditMatchInputValid() }
        addSource(mensHomePointsInputValid) { value = isMensAddOrEditMatchInputValid() }
        addSource(mensAwayTeamInputValid) { value = isMensAddOrEditMatchInputValid() }
        addSource(mensAwayPointsInputValid) { value = isMensAddOrEditMatchInputValid() }
        value = false
    }
    val mensAddOrEditMatchInputValid: LiveData<Boolean>
        get() = _mensAddOrEditMatchInputValid

    fun resetMensAddOrEditMatchInputValid() {
        _mensAddOrEditMatchInputValid.value = false
    }

    private fun isMensAddOrEditMatchInputValid() = mensHomeTeamInputValid.value == true && mensHomePointsInputValid.value == true
                    && mensAwayTeamInputValid.value == true && mensAwayPointsInputValid.value == true

    // Womens

    private val _womensEditingMatchResult = MutableLiveData<MatchResult>().apply { value = null }
    val womensEditingMatchResult: LiveData<MatchResult>
        get() = _womensEditingMatchResult

    private val _womensMatches = MediatorLiveData<List<MatchResult>>().apply {
        addSource(_womensEditingMatchResult) { womensEditingMatchResult ->
            val currentWomensMatches = value?.map { womensMatchResult ->
                val isEditing = womensEditingMatchResult != null && womensMatchResult.id == womensEditingMatchResult.id
                womensMatchResult.copy(isEditing = isEditing)
            }
            value = currentWomensMatches
        }
        value = null
    }
    val womensMatches: LiveData<List<MatchResult>>
        get() = _womensMatches

    val latestWomensWorldRugbyRankings = worldRugbyRankerRepository.getLatestWomensWorldRugbyRankings()
    private val _womensWorldRugbyRankings = MediatorLiveData<List<WorldRugbyRanking>>().apply {
        addSource(latestWomensWorldRugbyRankings) { womensWorldRugbyRankings ->
            if (!hasWomensMatches()) value = womensWorldRugbyRankings
        }
        addSource(_womensMatches) { womensMatches ->
            val latestWomensWorldRugbyRankings = latestWomensWorldRugbyRankings.value ?: return@addSource
            if (womensMatches == null) return@addSource
            value = RankingsCalculator.allocatePointsForMatchResults(
                    worldRugbyRankings = latestWomensWorldRugbyRankings,
                    matchResults = womensMatches
            )
        }
    }
    val womensWorldRugbyRankings: LiveData<List<WorldRugbyRanking>>
        get() = _womensWorldRugbyRankings

    fun hasWomensMatches() = !(_womensMatches.value?.isEmpty() ?: true)

    fun isEditingWomensMatch() = _womensEditingMatchResult.value != null

    fun addWomensMatchResult(matchResult: MatchResult) {
        val currentWomensMatches = (_womensMatches.value ?: emptyList()).toMutableList()
        currentWomensMatches.add(matchResult)
        _womensMatches.value = currentWomensMatches
    }

    fun beginEditWomensMatchResult(matchResult: MatchResult) {
        if (_womensEditingMatchResult.value == matchResult) return
        _womensEditingMatchResult.value = matchResult
    }

    fun endEditWomensMatchResult() {
        if (_womensEditingMatchResult.value == null) return
        _womensEditingMatchResult.value = null
    }

    fun editWomensMatchResult(matchResult: MatchResult) {
        val currentWomensMatches = _womensMatches.value!!.map { womensMatchResult ->
            if (womensMatchResult.id == matchResult.id) {
                matchResult
            } else {
                womensMatchResult
            }
        }
        _womensMatches.value = currentWomensMatches
    }

    fun removeWomensMatchResult(matchResult: MatchResult): Boolean {
        val removedWomensEditingMatchResult = _womensEditingMatchResult.value?.id == matchResult.id
        val currentWomensMatches = _womensMatches.value!!.toMutableList()
        currentWomensMatches.remove(matchResult)
        if (currentWomensMatches.isEmpty()) {
            resetWomens()
            return removedWomensEditingMatchResult
        }
        _womensMatches.value = currentWomensMatches
        if (removedWomensEditingMatchResult) _womensEditingMatchResult.value = null
        return removedWomensEditingMatchResult
    }

    private fun resetWomens() {
        _womensMatches.value = null
        _womensEditingMatchResult.value = null
        _womensWorldRugbyRankings.value = latestWomensWorldRugbyRankings.value
    }

    val womensHomeTeamInputValid = MutableLiveData<Boolean>()
    val womensHomePointsInputValid = MutableLiveData<Boolean>()
    val womensAwayTeamInputValid = MutableLiveData<Boolean>()
    val womensAwayPointsInputValid = MutableLiveData<Boolean>()

    private val _womensAddOrEditMatchInputValid = MediatorLiveData<Boolean>().apply {
        addSource(womensHomeTeamInputValid) { value = isWomensAddOrEditMatchInputValid() }
        addSource(womensHomePointsInputValid) { value = isWomensAddOrEditMatchInputValid() }
        addSource(womensAwayTeamInputValid) { value = isWomensAddOrEditMatchInputValid() }
        addSource(womensAwayPointsInputValid) { value = isWomensAddOrEditMatchInputValid() }
        value = false
    }
    val womensAddOrEditMatchInputValid: LiveData<Boolean>
        get() = _womensAddOrEditMatchInputValid

    fun resetWomensAddOrEditMatchInputValid() {
        _womensAddOrEditMatchInputValid.value = false
    }

    private fun isWomensAddOrEditMatchInputValid() = womensHomeTeamInputValid.value == true && womensHomePointsInputValid.value == true
            && womensAwayTeamInputValid.value == true && womensAwayPointsInputValid.value == true
}
