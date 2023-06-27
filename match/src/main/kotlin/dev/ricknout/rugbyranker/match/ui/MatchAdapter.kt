package dev.ricknout.rugbyranker.match.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import dev.ricknout.rugbyranker.match.databinding.ListItemMatchBinding
import dev.ricknout.rugbyranker.match.model.Match

class MatchAdapter(
    private val onPredictClick: (match: Match) -> Unit,
) : PagingDataAdapter<Match, MatchViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MatchViewHolder(
        ListItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    )

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = getItem(position) ?: return
        holder.bind(match, onPredictClick) { throw RuntimeException("Pin clicked from non-live match") }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Match>() {
            override fun areItemsTheSame(oldItem: Match, newItem: Match) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Match, newItem: Match) = oldItem == newItem
        }
    }
}
