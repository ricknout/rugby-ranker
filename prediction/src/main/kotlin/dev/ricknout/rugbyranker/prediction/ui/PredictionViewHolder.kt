package dev.ricknout.rugbyranker.prediction.ui

import androidx.recyclerview.widget.RecyclerView
import dev.ricknout.rugbyranker.prediction.R
import dev.ricknout.rugbyranker.prediction.databinding.ListItemPredictionBinding
import dev.ricknout.rugbyranker.prediction.model.Prediction

class PredictionViewHolder(private val binding: ListItemPredictionBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(prediction: Prediction, onClick: (prediction: Prediction) -> Unit, onCloseIconClick: (prediction: Prediction) -> Unit) {
        binding.chip.apply {
            text = context.getString(
                R.string.prediction,
                prediction.homeTeam.abbreviation,
                prediction.homeScore,
                prediction.awayScore,
                prediction.awayTeam.abbreviation,
            )
            when {
                prediction.rugbyWorldCup -> setChipIconResource(R.drawable.ic_rwc_24dp)
                prediction.noHomeAdvantage -> setChipIconResource(R.drawable.ic_nha_24dp)
                else -> chipIcon = null
            }
            setOnClickListener {
                onClick(prediction)
            }
            setOnCloseIconClickListener {
                onCloseIconClick(prediction)
            }
        }
    }
}
