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
        calculatedMensWorldRugbyRankings.value = null
        _mensWorldRugbyRankings.value = latestMensWorldRugbyRankings.value
    }

    val mensHomeTeamInputValid = MutableLiveData<Boolean>()
    val mensHomePointsInputValid = MutableLiveData<Boolean>()
    val mensAwayTeamInputValid = MutableLiveData<Boolean>()
    val mensAwayPointsInputValid = MutableLiveData<Boolean>()

    private val _mensAddMatchInputValid = MediatorLiveData<Boolean>().apply {
        addSource(mensHomeTeamInputValid) { value = isMensAddMatchInputValid() }
        addSource(mensHomePointsInputValid) { value = isMensAddMatchInputValid() }
        addSource(mensAwayTeamInputValid) { value = isMensAddMatchInputValid() }
        addSource(mensAwayPointsInputValid) { value = isMensAddMatchInputValid() }
        value = false
    }
    val mensAddMatchInputValid: LiveData<Boolean>
        get() = _mensAddMatchInputValid

    private fun isMensAddMatchInputValid() = mensHomeTeamInputValid.value == true && mensHomePointsInputValid.value == true
                    && mensAwayTeamInputValid.value == true && mensAwayPointsInputValid.value == true

    fun hasWomensMatches() = !(_mensMatches.value?.isEmpty() ?: true)

    private val _womensMatches = MutableLiveData<List<MatchResult>>().apply { value = null }
    val womensMatches: LiveData<List<MatchResult>>
        get() = _womensMatches

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
        calculatedWomensWorldRugbyRankings.value = null
        _womensWorldRugbyRankings.value = latestWomensWorldRugbyRankings.value
    }

    val womensHomeTeamInputValid = MutableLiveData<Boolean>()
    val womensHomePointsInputValid = MutableLiveData<Boolean>()
    val womensAwayTeamInputValid = MutableLiveData<Boolean>()
    val womensAwayPointsInputValid = MutableLiveData<Boolean>()

    private val _womensAddMatchInputValid = MediatorLiveData<Boolean>().apply {
        addSource(womensHomeTeamInputValid) { value = isWomensAddMatchInputValid() }
        addSource(womensHomePointsInputValid) { value = isWomensAddMatchInputValid() }
        addSource(womensAwayTeamInputValid) { value = isWomensAddMatchInputValid() }
        addSource(womensAwayPointsInputValid) { value = isWomensAddMatchInputValid() }
        value = false
    }
    val womensAddMatchInputValid: LiveData<Boolean>
        get() = _womensAddMatchInputValid

    private fun isWomensAddMatchInputValid() = womensHomeTeamInputValid.value == true && womensHomePointsInputValid.value == true
            && womensAwayTeamInputValid.value == true && womensAwayPointsInputValid.value == true
}