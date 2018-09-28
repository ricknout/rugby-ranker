package com.ricknout.worldrugbyranker.ui.ranking

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

    private val _isCalculatingMens = MutableLiveData<Boolean>().apply { value = false }
    val isCalculatingMens: LiveData<Boolean>
        get() = _isCalculatingMens

    private fun isCalculatingMens() = _isCalculatingMens.value == true

    private val latestMensWorldRugbyRankings = worldRugbyRankerRepository.getLatestMensWorldRugbyRankings()
    private val calculatedMensWorldRugbyRankings = MutableLiveData<List<WorldRugbyRanking>>()
    val mensWorldRugbyRankings = MediatorLiveData<List<WorldRugbyRanking>>().apply {
        addSource(latestMensWorldRugbyRankings) { mensWorldRugbyRankings ->
            if (!isCalculatingMens()) value = mensWorldRugbyRankings
        }
        addSource(calculatedMensWorldRugbyRankings) { mensWorldRugbyRankings ->
            if (isCalculatingMens()) value = mensWorldRugbyRankings
        }
    }

    fun calculateMens(matchResult: MatchResult) {
        val currentMensWorldRugbyRankings = calculatedMensWorldRugbyRankings.value ?: latestMensWorldRugbyRankings.value ?: return
        _isCalculatingMens.value = true
        calculatedMensWorldRugbyRankings.value = RankingsCalculator.allocatePointsForMatchResult(
                worldRugbyRankings = currentMensWorldRugbyRankings,
                matchResult = matchResult
        )
    }

    fun resetMens() {
        _isCalculatingMens.value = false
        calculatedMensWorldRugbyRankings.value = null
        mensWorldRugbyRankings.value = latestMensWorldRugbyRankings.value
    }

    private val _isCalculatingWomens = MutableLiveData<Boolean>().apply { value = false }
    val isCalculatingWomens: LiveData<Boolean>
        get() = _isCalculatingWomens

    private fun isCalculatingWomens() = _isCalculatingMens.value == true

    private val latestWomensWorldRugbyRankings = worldRugbyRankerRepository.getLatestWomensWorldRugbyRankings()
    private val calculatedWomensWorldRugbyRankings = MutableLiveData<List<WorldRugbyRanking>>()
    val womensWorldRugbyRankings = MediatorLiveData<List<WorldRugbyRanking>>().apply {
        addSource(latestWomensWorldRugbyRankings) { womensWorldRugbyRankings ->
            if (!isCalculatingWomens()) value = womensWorldRugbyRankings
        }
        addSource(calculatedWomensWorldRugbyRankings) { womensWorldRugbyRankings ->
            if (isCalculatingWomens()) value = womensWorldRugbyRankings
        }
    }

    fun calculateWomens(matchResult: MatchResult) {
        val currentWomensWorldRugbyRankings = calculatedWomensWorldRugbyRankings.value ?: latestWomensWorldRugbyRankings.value ?: return
        _isCalculatingWomens.value = true
        calculatedWomensWorldRugbyRankings.value = RankingsCalculator.allocatePointsForMatchResult(
                worldRugbyRankings = currentWomensWorldRugbyRankings,
                matchResult = matchResult
        )
    }

    fun resetWomens() {
        _isCalculatingWomens.value = false
        calculatedWomensWorldRugbyRankings.value = null
        womensWorldRugbyRankings.value = latestWomensWorldRugbyRankings.value
    }
}