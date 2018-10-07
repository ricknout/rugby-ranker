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

    fun hasMensMatches() = !(_mensMatches.value?.isEmpty() ?: true)

    private val _mensMatches = MutableLiveData<List<MatchResult>>().apply { value = null }
    val mensMatches: LiveData<List<MatchResult>>
        get() = _mensMatches

    fun isEditingMensMatch() = _editingMensMatchResult.value != null

    private val _editingMensMatchResult = MutableLiveData<MatchResult>().apply { value = null }
    val editingMensMatchResult: LiveData<MatchResult>
        get() = _editingMensMatchResult

    val latestMensWorldRugbyRankings = worldRugbyRankerRepository.getLatestMensWorldRugbyRankings()
    private val calculatedMensWorldRugbyRankings = MutableLiveData<List<WorldRugbyRanking>>()
    private val _mensWorldRugbyRankings = MediatorLiveData<List<WorldRugbyRanking>>().apply {
        addSource(latestMensWorldRugbyRankings) { mensWorldRugbyRankings ->
            if (!hasMensMatches()) value = mensWorldRugbyRankings
        }
        addSource(calculatedMensWorldRugbyRankings) { mensWorldRugbyRankings ->
            if (hasMensMatches()) value = mensWorldRugbyRankings
        }
    }
    val mensWorldRugbyRankings: LiveData<List<WorldRugbyRanking>>
        get() = _mensWorldRugbyRankings

    fun addMensMatchResult(matchResult: MatchResult) {
        val latestMensWorldRugbyRankings = latestMensWorldRugbyRankings.value ?: return
        val currentMensMatches = (_mensMatches.value ?: emptyList()).toMutableList()
        currentMensMatches.add(matchResult)
        _mensMatches.value = currentMensMatches
        calculatedMensWorldRugbyRankings.value = RankingsCalculator.allocatePointsForMatchResults(
                worldRugbyRankings = latestMensWorldRugbyRankings,
                matchResults = currentMensMatches
        )
    }

    fun beginEditMensMatchResult(matchResult: MatchResult) {
        _editingMensMatchResult.value = matchResult
    }

    fun endEditMensMatchResult() {
        _editingMensMatchResult.value = null
    }

    fun editMensMatchResult(matchResult: MatchResult) {
        val latestMensWorldRugbyRankings = latestMensWorldRugbyRankings.value ?: return
        val currentMensMatches = _mensMatches.value!!.map { mensMatchResult ->
            if (mensMatchResult.id == matchResult.id) {
                matchResult
            } else {
                mensMatchResult
            }
        }
        _mensMatches.value = currentMensMatches
        calculatedMensWorldRugbyRankings.value = RankingsCalculator.allocatePointsForMatchResults(
                worldRugbyRankings = latestMensWorldRugbyRankings,
                matchResults = currentMensMatches
        )
    }

    fun removeMensMatchResult(matchResult: MatchResult) {
        val latestMensWorldRugbyRankings = latestMensWorldRugbyRankings.value ?: return
        val currentMensMatches = (_mensMatches.value ?: emptyList()).toMutableList()
        currentMensMatches.remove(matchResult)
        if (currentMensMatches.isEmpty()) {
            resetMens()
            return
        }
        _mensMatches.value = currentMensMatches
        calculatedMensWorldRugbyRankings.value = RankingsCalculator.allocatePointsForMatchResults(
                worldRugbyRankings = latestMensWorldRugbyRankings,
                matchResults = currentMensMatches
        )
    }

    fun resetMens() {
        _mensMatches.value = null
        _editingMensMatchResult.value = null
        calculatedMensWorldRugbyRankings.value = null
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

    fun hasWomensMatches() = !(_womensMatches.value?.isEmpty() ?: true)

    private val _womensMatches = MutableLiveData<List<MatchResult>>().apply { value = null }
    val womensMatches: LiveData<List<MatchResult>>
        get() = _womensMatches

    fun isEditingWomensMatch() = _editingWomensMatchResult.value != null

    private val _editingWomensMatchResult = MutableLiveData<MatchResult>().apply { value = null }
    val editingWomensMatchResult: LiveData<MatchResult>
        get() = _editingWomensMatchResult

    val latestWomensWorldRugbyRankings = worldRugbyRankerRepository.getLatestWomensWorldRugbyRankings()
    private val calculatedWomensWorldRugbyRankings = MutableLiveData<List<WorldRugbyRanking>>()
    private val _womensWorldRugbyRankings = MediatorLiveData<List<WorldRugbyRanking>>().apply {
        addSource(latestWomensWorldRugbyRankings) { womensWorldRugbyRankings ->
            if (!hasWomensMatches()) value = womensWorldRugbyRankings
        }
        addSource(calculatedWomensWorldRugbyRankings) { womensWorldRugbyRankings ->
            if (hasWomensMatches()) value = womensWorldRugbyRankings
        }
    }
    val womensWorldRugbyRankings: LiveData<List<WorldRugbyRanking>>
        get() = _womensWorldRugbyRankings

    fun addWomensMatchResult(matchResult: MatchResult) {
        val latestWomensWorldRugbyRankings = latestWomensWorldRugbyRankings.value ?: return
        val currentWomensMatches = (_womensMatches.value ?: emptyList()).toMutableList()
        currentWomensMatches.add(matchResult)
        _womensMatches.value = currentWomensMatches
        calculatedWomensWorldRugbyRankings.value = RankingsCalculator.allocatePointsForMatchResults(
                worldRugbyRankings = latestWomensWorldRugbyRankings,
                matchResults = currentWomensMatches
        )
    }

    fun beginEditWomensMatchResult(matchResult: MatchResult) {
        _editingWomensMatchResult.value = matchResult
    }

    fun endEditWomensMatchResult() {
        _editingWomensMatchResult.value = null
    }

    fun editWomensMatchResult(matchResult: MatchResult) {
        val latestWomensWorldRugbyRankings = latestWomensWorldRugbyRankings.value ?: return
        val currentWomensMatches = _womensMatches.value!!.map { womensMatchResult ->
            if (womensMatchResult.id == matchResult.id) {
                matchResult
            } else {
                womensMatchResult
            }
        }
        _womensMatches.value = currentWomensMatches
        calculatedWomensWorldRugbyRankings.value = RankingsCalculator.allocatePointsForMatchResults(
                worldRugbyRankings = latestWomensWorldRugbyRankings,
                matchResults = currentWomensMatches
        )
    }

    fun removeWomensMatchResult(matchResult: MatchResult) {
        val latestWomensWorldRugbyRankings = latestWomensWorldRugbyRankings.value ?: return
        val currentWomensMatches = (_womensMatches.value ?: emptyList()).toMutableList()
        currentWomensMatches.remove(matchResult)
        if (currentWomensMatches.isEmpty()) {
            resetWomens()
            return
        }
        _womensMatches.value = currentWomensMatches
        calculatedWomensWorldRugbyRankings.value = RankingsCalculator.allocatePointsForMatchResults(
                worldRugbyRankings = latestWomensWorldRugbyRankings,
                matchResults = currentWomensMatches
        )
    }

    fun resetWomens() {
        _womensMatches.value = null
        _editingWomensMatchResult.value = null
        calculatedWomensWorldRugbyRankings.value = null
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
