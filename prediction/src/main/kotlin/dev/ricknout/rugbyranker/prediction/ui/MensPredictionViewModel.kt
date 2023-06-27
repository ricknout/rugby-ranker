package dev.ricknout.rugbyranker.prediction.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.prediction.data.PredictionRepository
import javax.inject.Inject

@HiltViewModel
class MensPredictionViewModel @Inject constructor(
    repository: PredictionRepository,
) : PredictionViewModel(Sport.MENS, repository)
