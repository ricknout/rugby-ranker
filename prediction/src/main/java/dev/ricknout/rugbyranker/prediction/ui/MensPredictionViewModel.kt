package dev.ricknout.rugbyranker.prediction.ui

import androidx.hilt.lifecycle.ViewModelInject
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.prediction.data.PredictionRepository

class MensPredictionViewModel @ViewModelInject constructor(
    repository: PredictionRepository
) : PredictionViewModel(Sport.MENS, repository)
