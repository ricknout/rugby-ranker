package com.ricknout.rugbyranker.prediction.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ricknout.rugbyranker.prediction.R
import com.ricknout.rugbyranker.prediction.vo.MatchPrediction
import kotlinx.android.synthetic.main.list_item_match_prediction.view.*

class MatchPredictionListAdapter(
    private val onItemClick: (matchPrediction: MatchPrediction) -> Unit,
    private val onItemCloseIconClick: (matchPrediction: MatchPrediction) -> Unit
) : ListAdapter<MatchPrediction, MatchPredictionViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchPredictionViewHolder =
            MatchPredictionViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_match_prediction, parent, false))

    override fun onBindViewHolder(holder: MatchPredictionViewHolder, position: Int) {
        val matchPrediction = getItem(position)
        holder.bind(matchPrediction, onItemClick, onItemCloseIconClick)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MatchPrediction>() {
            override fun areItemsTheSame(oldItem: MatchPrediction, newItem: MatchPrediction) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: MatchPrediction, newItem: MatchPrediction) = oldItem == newItem
        }
    }
}

class MatchPredictionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(matchPrediction: MatchPrediction, onItemClick: (matchPrediction: MatchPrediction) -> Unit, onItemCloseIconClick: (matchPrediction: MatchPrediction) -> Unit) {
        itemView.chip.apply {
            when {
                matchPrediction.isEditing -> setChipIconResource(R.drawable.ic_edit_black_24dp)
                matchPrediction.rugbyWorldCup -> setChipIconResource(R.drawable.ic_rwc_black_24dp)
                matchPrediction.noHomeAdvantage -> setChipIconResource(R.drawable.ic_nha_black_24dp)
                else -> chipIcon = null
            }
            text = itemView.context.getString(R.string.chip_match_prediction,
                    matchPrediction.homeTeamAbbreviation, matchPrediction.homeTeamScore, matchPrediction.awayTeamScore, matchPrediction.awayTeamAbbreviation)
            val backgroundColorResId = if (matchPrediction.isEditing) {
                R.color.color_secondary_variant
            } else {
                R.color.color_secondary
            }
            itemView.chip.setChipBackgroundColorResource(backgroundColorResId)
            setOnClickListener {
                onItemClick(matchPrediction)
            }
            setOnCloseIconClickListener {
                onItemCloseIconClick(matchPrediction)
            }
        }
    }
}
