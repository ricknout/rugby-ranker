package com.ricknout.rugbyranker.prediction.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ricknout.rugbyranker.prediction.R
import com.ricknout.rugbyranker.prediction.vo.Prediction
import kotlinx.android.synthetic.main.list_item_prediction.view.*

class PredictionListAdapter(
    private val onItemClick: (prediction: Prediction) -> Unit,
    private val onItemCloseIconClick: (prediction: Prediction) -> Unit
) : ListAdapter<Prediction, PredictionViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionViewHolder =
            PredictionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_prediction, parent, false))

    override fun onBindViewHolder(holder: PredictionViewHolder, position: Int) {
        val prediction = getItem(position)
        holder.bind(prediction, onItemClick, onItemCloseIconClick)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Prediction>() {
            override fun areItemsTheSame(oldItem: Prediction, newItem: Prediction) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Prediction, newItem: Prediction) = oldItem == newItem
        }
    }
}

class PredictionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(prediction: Prediction, onItemClick: (prediction: Prediction) -> Unit, onItemCloseIconClick: (prediction: Prediction) -> Unit) {
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
