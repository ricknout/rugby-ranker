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
        val entry = getItem(position)
        holder.bind(entry)
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
        itemView.textView.text = "${worldRugbyRanking.position} ${worldRugbyRanking.teamName} ${worldRugbyRanking.points}"
    }
}
