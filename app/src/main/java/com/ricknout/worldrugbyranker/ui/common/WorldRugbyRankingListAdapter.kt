package com.ricknout.worldrugbyranker.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.emoji.text.EmojiCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ricknout.worldrugbyranker.R
import com.ricknout.worldrugbyranker.util.FlagUtils
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
        itemView.positionTextView.text = "${worldRugbyRanking.position}"
        val previousPosition = "(${worldRugbyRanking.previousPosition})"
        when {
            worldRugbyRanking.position > worldRugbyRanking.previousPosition -> {
                itemView.previousPositionTextView.text = previousPosition
                itemView.previousPositionTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.world_rugby_red))
            }
            worldRugbyRanking.position < worldRugbyRanking.previousPosition -> {
                itemView.previousPositionTextView.text = previousPosition
                itemView.previousPositionTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.world_rugby_green))
            }
            else -> itemView.previousPositionTextView.text = ""
        }
        val flag = EmojiCompat.get().process(FlagUtils.getFlagEmojiForTeamAbbreviation(worldRugbyRanking.teamAbbreviation))
        itemView.flagTextView.text = flag
        itemView.teamTextView.text = worldRugbyRanking.teamName
        val pointsFormat = "%.2f"
        itemView.pointsTextView.text = pointsFormat.format(worldRugbyRanking.points)
        val previousPoints = "(${pointsFormat.format(worldRugbyRanking.previousPoints)})"
        when {
            worldRugbyRanking.points > worldRugbyRanking.previousPoints -> {
                itemView.previousPointsTextView.text = previousPoints
                itemView.previousPointsTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.world_rugby_green))
            }
            worldRugbyRanking.points < worldRugbyRanking.previousPoints -> {
                itemView.previousPointsTextView.text = previousPoints
                itemView.previousPointsTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.world_rugby_red))
            }
            else -> itemView.previousPointsTextView.text = ""
        }
        val backgroundColor = if (adapterPosition == 0 || adapterPosition % 2 == 0) {
            ContextCompat.getColor(itemView.context, R.color.light_grey)
        } else {
            ContextCompat.getColor(itemView.context, R.color.white)
        }
        itemView.backgroundView.setBackgroundColor(backgroundColor)
    }
}
