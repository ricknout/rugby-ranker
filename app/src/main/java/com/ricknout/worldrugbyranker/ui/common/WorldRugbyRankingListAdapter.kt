package com.ricknout.worldrugbyranker.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ricknout.worldrugbyranker.R
import com.ricknout.worldrugbyranker.vo.WorldRugbyRanking
import kotlinx.android.synthetic.main.list_item_world_rugby_ranking.view.*

class WorldRugbyRankingListAdapter : ListAdapter<WorldRugbyRanking, WorldRugbyRankingViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorldRugbyRankingViewHolder
            = WorldRugbyRankingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_world_rugby_ranking, parent, false))

    override fun onBindViewHolder(holder: WorldRugbyRankingViewHolder, position: Int) {
        val worldRugbyRanking = getItem(position)
        holder.bind(worldRugbyRanking)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WorldRugbyRanking>() {
            override fun areItemsTheSame(oldItem: WorldRugbyRanking, newItem: WorldRugbyRanking) = oldItem.teamId == newItem.teamId
            override fun areContentsTheSame(oldItem: WorldRugbyRanking, newItem: WorldRugbyRanking) = oldItem == newItem
        }
    }
}

class WorldRugbyRankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(worldRugbyRanking: WorldRugbyRanking) {
        val previousPosition = if (worldRugbyRanking.position != worldRugbyRanking.previousPosition) {
            " (${worldRugbyRanking.previousPosition})"
        } else {
            ""
        }
        val previousPoints = if (worldRugbyRanking.points != worldRugbyRanking.previousPoints) {
            " (${worldRugbyRanking.previousPoints})"
        } else {
            ""
        }
        itemView.textView.text = "${worldRugbyRanking.position}$previousPosition ${worldRugbyRanking.teamName} ${worldRugbyRanking.points}$previousPoints"
    }
}
