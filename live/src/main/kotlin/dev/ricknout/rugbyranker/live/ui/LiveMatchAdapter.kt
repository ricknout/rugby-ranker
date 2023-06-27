package dev.ricknout.rugbyranker.live.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.ricknout.rugbyranker.match.databinding.ListItemMatchBinding
import dev.ricknout.rugbyranker.match.model.Match
import dev.ricknout.rugbyranker.match.ui.MatchViewHolder

class LiveMatchAdapter(
    private val onPredictClick: (match: Match) -> Unit,
    private val onPinClick: (match: Match) -> Unit,
) : ListAdapter<Match, MatchViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MatchViewHolder(
        ListItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    )

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = getItem(position) ?: return
        holder.bind(match, onPredictClick, onPinClick)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Match>() {
            override fun areItemsTheSame(oldItem: Match, newItem: Match) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Match, newItem: Match) = oldItem == newItem
        }
    }
}
