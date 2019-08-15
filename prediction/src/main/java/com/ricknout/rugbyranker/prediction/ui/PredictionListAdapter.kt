package com.ricknout.rugbyranker.prediction.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ricknout.rugbyranker.prediction.R
import com.ricknout.rugbyranker.prediction.vo.MatchPrediction
import kotlinx.android.synthetic.main.list_item_prediction.view.*

class PredictionListAdapter(
    private val onItemClick: (prediction: MatchPrediction) -> Unit,
    private val onItemCloseIconClick: (prediction: MatchPrediction) -> Unit
) : ListAdapter<MatchPrediction, PredictionViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionViewHolder =
            PredictionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_prediction, parent, false))

    override fun onBindViewHolder(holder: PredictionViewHolder, position: Int) {
        val prediction = getItem(position)
        holder.bind(prediction, onItemClick, onItemCloseIconClick)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MatchPrediction>() {
            override fun areItemsTheSame(oldItem: MatchPrediction, newItem: MatchPrediction) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: MatchPrediction, newItem: MatchPrediction) = oldItem == newItem
        }
    }
}

class PredictionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(prediction: MatchPrediction, onItemClick: (prediction: MatchPrediction) -> Unit, onItemCloseIconClick: (prediction: MatchPrediction) -> Unit) {
        itemView.chip.apply {
            when {
                prediction.rugbyWorldCup -> setChipIconResource(R.drawable.ic_rwc_black_24dp)
                prediction.noHomeAdvantage -> setChipIconResource(R.drawable.ic_nha_black_24dp)
                else -> chipIcon = null
            }
            text = itemView.context.getString(R.string.chip_prediction,
                    prediction.homeTeamAbbreviation, prediction.homeTeamScore, prediction.awayTeamScore, prediction.awayTeamAbbreviation)
            setOnClickListener {
                onItemClick(prediction)
            }
            setOnCloseIconClickListener {
                onItemCloseIconClick(prediction)
            }
        }
    }
}
