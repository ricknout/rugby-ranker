package com.ricknout.worldrugbyranker.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ricknout.worldrugbyranker.R
import com.ricknout.worldrugbyranker.vo.MatchResult
import kotlinx.android.synthetic.main.list_item_match_result.view.*

class MatchResultListAdapter(
        private val onItemCloseIconClick: (matchResult: MatchResult) -> Unit
) : ListAdapter<MatchResult, MatchResultViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchResultViewHolder
            = MatchResultViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_match_result, parent, false))

    override fun onBindViewHolder(holder: MatchResultViewHolder, position: Int) {
        val matchResult = getItem(position)
        holder.bind(matchResult, onItemCloseIconClick)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MatchResult>() {
            override fun areItemsTheSame(oldItem: MatchResult, newItem: MatchResult) = oldItem == newItem
            override fun areContentsTheSame(oldItem: MatchResult, newItem: MatchResult) = oldItem == newItem
        }
    }
}

class MatchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(matchResult: MatchResult, onItemCloseIconClick: (matchResult: MatchResult) -> Unit) {
        itemView.chip.apply {
            when {
                matchResult.rugbyWorldCup -> setChipIconResource(R.drawable.ic_rwc_white_24dp)
                matchResult.noHomeAdvantage -> setChipIconResource(R.drawable.ic_nha_white_24dp)
                else -> chipIcon = null
            }
            text = "${matchResult.homeTeamAbbreviation} ${matchResult.homeTeamScore} - ${matchResult.awayTeamScore} ${matchResult.awayTeamAbbreviation}"
            setOnCloseIconClickListener {
                onItemCloseIconClick(matchResult)
            }
        }
    }
}
