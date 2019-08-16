package com.ricknout.rugbyranker.prediction.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.prediction.vo.Prediction

open class PredictionViewModel(private val sport: Sport) : ViewModel() {

    private val _predictions = MediatorLiveData<List<Prediction>>()
    val predictions: LiveData<List<Prediction>>
        get() = _predictions

    fun addPrediction(prediction: Prediction) {
        val currentPredictions = (_predictions.value ?: emptyList()).toMutableList()
        currentPredictions.add(prediction)
        _predictions.value = currentPredictions
    }

    fun editPrediction(prediction: Prediction) {
        val currentPredictions = _predictions.value!!.map { currentPrediction ->
            if (currentPrediction.id == prediction.id) {
                prediction
            } else {
                currentPrediction
            }
        }
        _predictions.value = currentPredictions
    }

    fun removePrediction(prediction: Prediction) {
        val currentPredictions = _predictions.value!!.toMutableList()
        currentPredictions.remove(prediction)
        _predictions.value = currentPredictions
    }

    fun hasPredictions() = !(_predictions.value?.isEmpty() ?: true)

    fun getPredictionCount() = _predictions.value?.size ?: 0

    val homeTeamInputValid = MutableLiveData<Boolean>()
    val homePointsInputValid = MutableLiveData<Boolean>()
    val awayTeamInputValid = MutableLiveData<Boolean>()
    val awayPointsInputValid = MutableLiveData<Boolean>()

    private val _predictionInputValid = MediatorLiveData<Boolean>().apply {
        addSource(homeTeamInputValid) { value = isPredictionInputValid() }
        addSource(homePointsInputValid) { value = isPredictionInputValid() }
        addSource(awayTeamInputValid) { value = isPredictionInputValid() }
        addSource(awayPointsInputValid) { value = isPredictionInputValid() }
        value = false
    }
    val predictionInputValid: LiveData<Boolean>
        get() = _predictionInputValid

    private fun isPredictionInputValid() =
            homeTeamInputValid.value == true &&
                    homePointsInputValid.value == true &&
                    awayTeamInputValid.value == true &&
                    awayPointsInputValid.value == true

    fun resetPredictionInputValid() {
        homeTeamInputValid.value = false
        homePointsInputValid.value = false
        awayTeamInputValid.value = false
        awayPointsInputValid.value = false
    }
}
