package com.ricknout.rugbyranker.matches.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ricknout.rugbyranker.matches.R
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch

class WorldRugbyMatchPagedListAdapter(
    private val onItemCountChange: () -> Unit,
    private val onPredictClick: (worldRugbyMatch: WorldRugbyMatch) -> Unit
) : PagedListAdapter<WorldRugbyMatch, WorldRugbyMatchViewHolder>(DIFF_CALLBACK) {

    private var currentItemCount = 0

    var worldRugbyRankingsTeamIds: Map<Long, Boolean> = emptyMap()
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WorldRugbyMatchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_world_rugby_match, parent, false))

    override fun onBindViewHolder(holder: WorldRugbyMatchViewHolder, position: Int) {
        val worldRugbyMatch = getItem(position) ?: return
        val predictable = worldRugbyRankingsTeamIds[worldRugbyMatch.firstTeamId] == true && worldRugbyRankingsTeamIds[worldRugbyMatch.secondTeamId] == true
        holder.bind(worldRugbyMatch, predictable, onPredictClick)
    }

    override fun getItemCount(): Int {
        val itemCount = super.getItemCount()
        if (currentItemCount != itemCount) {
            onItemCountChange.invoke()
            currentItemCount = itemCount
        }
        return itemCount
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WorldRugbyMatch>() {
            override fun areItemsTheSame(oldItem: WorldRugbyMatch, newItem: WorldRugbyMatch) = oldItem.matchId == newItem.matchId
            override fun areContentsTheSame(oldItem: WorldRugbyMatch, newItem: WorldRugbyMatch) = oldItem == newItem
        }
    }
}
