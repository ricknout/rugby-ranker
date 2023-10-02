package dev.ricknout.rugbyranker.prediction.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.prediction.data.PredictionRepository
import dev.ricknout.rugbyranker.prediction.model.Prediction
import dev.ricknout.rugbyranker.prediction.model.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

open class PredictionViewModel(
    sport: Sport,
    repository: PredictionRepository,
) : ViewModel() {
    val teams =
        repository.loadRankings(sport)
            .map { rankings ->
                rankings.map { ranking ->
                    Team(ranking.teamId, ranking.teamName, ranking.teamAbbreviation)
                }
            }
            .asLiveData()

    private val _homeTeam = MutableStateFlow<Team?>(null)
    val homeTeam: LiveData<Team?> = _homeTeam.asLiveData()
    private val _awayTeam = MutableStateFlow<Team?>(null)
    val awayTeam: LiveData<Team?> = _awayTeam.asLiveData()
    private val _homeScore = MutableStateFlow(0)
    val homeScore: LiveData<Int> = _homeScore.asLiveData()
    private val _awayScore = MutableStateFlow(0)
    val awayScore: LiveData<Int> = _awayScore.asLiveData()
    private val _rugbyWorldCup = MutableStateFlow(false)
    val rugbyWorldCup: LiveData<Boolean> = _rugbyWorldCup.asLiveData()
    private val _noHomeAdvantage = MutableStateFlow(false)
    val noHomeAdvantage: LiveData<Boolean> = _noHomeAdvantage.asLiveData()

    val inputValid =
        combine(_homeTeam, _awayTeam) { homeTeam, awayTeam ->
            homeTeam != null && awayTeam != null
        }.asLiveData()

    private val _predictions = MutableStateFlow<List<Prediction>>(emptyList())
    val predictions: LiveData<List<Prediction>> = _predictions.asLiveData()

    fun submitPrediction(
        prediction: Prediction? = null,
        edit: Boolean,
    ) {
        val p = buildPrediction(prediction)
        if (edit) editPrediction(prediction = p) else addPrediction(prediction = p)
    }

    fun removePrediction(prediction: Prediction) {
        val predictions = _predictions.value.toMutableList()
        predictions.remove(prediction)
        _predictions.value = predictions
    }

    fun containsPredictionWithId(prediction: Prediction): Boolean {
        val predictions = _predictions.value
        return predictions.any { p -> p.id == prediction.id }
    }

    fun hasPredictions() = _predictions.value.isNotEmpty()

    fun getPredictionCount() = _predictions.value.size

    fun setInput(
        homeTeam: Team? = _homeTeam.value,
        awayTeam: Team? = _awayTeam.value,
        homeScore: Int = _homeScore.value,
        awayScore: Int = _awayScore.value,
        rugbyWorldCup: Boolean = _rugbyWorldCup.value,
        noHomeAdvantage: Boolean = _noHomeAdvantage.value,
        validateTeams: Boolean = true,
    ) {
        if (!validateTeams || _awayTeam.value != homeTeam) _homeTeam.value = homeTeam
        if (!validateTeams || _homeTeam.value != awayTeam) _awayTeam.value = awayTeam
        _homeScore.value = homeScore
        _awayScore.value = awayScore
        _rugbyWorldCup.value = rugbyWorldCup
        _noHomeAdvantage.value = noHomeAdvantage
    }

    fun setInput(prediction: Prediction) =
        setInput(
            homeTeam = prediction.homeTeam,
            awayTeam = prediction.awayTeam,
            homeScore = prediction.homeScore,
            awayScore = prediction.awayScore,
            rugbyWorldCup = prediction.rugbyWorldCup,
            noHomeAdvantage = prediction.noHomeAdvantage,
            validateTeams = false,
        )

    fun resetInput() =
        setInput(
            homeTeam = null,
            awayTeam = null,
            homeScore = 0,
            awayScore = 0,
            rugbyWorldCup = false,
            noHomeAdvantage = false,
            validateTeams = false,
        )

    fun incrementHomeScore() {
        val score = _homeScore.value.inc().coerceIn(MIN_SCORE, MAX_SCORE)
        _homeScore.value = score
    }

    fun decrementHomeScore() {
        val score = _homeScore.value.dec().coerceIn(MIN_SCORE, MAX_SCORE)
        _homeScore.value = score
    }

    fun incrementAwayScore() {
        val score = _awayScore.value.inc().coerceIn(MIN_SCORE, MAX_SCORE)
        _awayScore.value = score
    }

    fun decrementAwayScore() {
        val score = _awayScore.value.dec().coerceIn(MIN_SCORE, MAX_SCORE)
        _awayScore.value = score
    }

    private fun addPrediction(prediction: Prediction) {
        val predictions = _predictions.value.toMutableList()
        predictions.add(prediction)
        _predictions.value = predictions
    }

    private fun editPrediction(prediction: Prediction) {
        val predictions =
            _predictions.value.map {
                if (it.id == prediction.id) {
                    prediction
                } else {
                    it
                }
            }
        _predictions.value = predictions
    }

    private fun buildPrediction(prediction: Prediction? = null): Prediction {
        return Prediction(
            id = prediction?.id ?: Prediction.generateId(),
            homeTeam = _homeTeam.value!!,
            homeScore = _homeScore.value,
            awayTeam = _awayTeam.value!!,
            awayScore = _awayScore.value,
            rugbyWorldCup = _rugbyWorldCup.value,
            noHomeAdvantage = _noHomeAdvantage.value,
        )
    }

    companion object {
        private const val MIN_SCORE = 0
        private const val MAX_SCORE = 200
    }
}
