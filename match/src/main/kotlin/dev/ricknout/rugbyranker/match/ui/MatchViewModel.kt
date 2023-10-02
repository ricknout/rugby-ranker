package dev.ricknout.rugbyranker.match.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dev.ricknout.rugbyranker.core.lifecycle.ScrollableViewModel
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.match.data.MatchRepository
import dev.ricknout.rugbyranker.match.model.Status
import dev.ricknout.rugbyranker.prediction.model.Prediction
import kotlinx.coroutines.flow.MutableStateFlow

open class MatchViewModel(
    sport: Sport,
    status: Status,
    repository: MatchRepository,
) : ScrollableViewModel() {
    val matches = repository.loadMatches(sport, status).cachedIn(viewModelScope).asLiveData()

    private val _predict = MutableStateFlow<Prediction?>(null)
    val predict: LiveData<Prediction?> = _predict.asLiveData()

    fun predict(prediction: Prediction) {
        _predict.value = prediction
    }

    fun resetPredict() {
        _predict.value = null
    }
}
