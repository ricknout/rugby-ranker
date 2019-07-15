package com.ricknout.rugbyranker.rankings.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.emoji.text.EmojiCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ricknout.rugbyranker.core.util.FlagUtils
import com.ricknout.rugbyranker.rankings.R
import com.ricknout.rugbyranker.rankings.vo.WorldRugbyRanking
import kotlin.math.abs
import kotlinx.android.synthetic.main.list_item_world_rugby_ranking.view.*

class WorldRugbyRankingListAdapter : ListAdapter<WorldRugbyRanking, WorldRugbyRankingViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            WorldRugbyRankingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_world_rugby_ranking, parent, false))

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
        val worldRugbyGreenColor = ContextCompat.getColor(itemView.context, R.color.color_primary)
        val worldRugbyRedColor = ContextCompat.getColor(itemView.context, R.color.color_error)
        when {
            worldRugbyRanking.position > worldRugbyRanking.previousPosition -> {
                val downPreviousPosition = itemView.context.getString(R.string.ranking_previous_position_down, worldRugbyRanking.previousPosition)
                itemView.previousPositionTextView.text = downPreviousPosition
                itemView.previousPositionTextView.setTextColor(worldRugbyRedColor)
            }
            worldRugbyRanking.position < worldRugbyRanking.previousPosition -> {
                val upPreviousPosition = itemView.context.getString(R.string.ranking_previous_position_up, worldRugbyRanking.previousPosition)
                itemView.previousPositionTextView.text = upPreviousPosition
                itemView.previousPositionTextView.setTextColor(worldRugbyGreenColor)
            }
            else -> itemView.previousPositionTextView.text = ""
        }
        val flag = EmojiCompat.get().process(FlagUtils.getFlagEmojiForTeamAbbreviation(worldRugbyRanking.teamAbbreviation))
        itemView.flagTextView.text = flag
        itemView.teamTextView.text = worldRugbyRanking.teamName
        val pointsFormat = "%.2f"
        itemView.pointsTextView.text = pointsFormat.format(worldRugbyRanking.points)
        val pointsDifference = pointsFormat.format(abs(worldRugbyRanking.pointsDifference()))
        when {
            worldRugbyRanking.points > worldRugbyRanking.previousPoints -> {
                val positivePointsDifference = itemView.context.getString(R.string.ranking_points_difference_positive, pointsDifference)
                itemView.pointsDifferenceTextView.text = positivePointsDifference
                itemView.pointsDifferenceTextView.setTextColor(worldRugbyGreenColor)
            }
            worldRugbyRanking.points < worldRugbyRanking.previousPoints -> {
                val negativePointsDifference = itemView.context.getString(R.string.ranking_points_difference_negative, pointsDifference)
                itemView.pointsDifferenceTextView.text = negativePointsDifference
                itemView.pointsDifferenceTextView.setTextColor(worldRugbyRedColor)
            }
            else -> itemView.pointsDifferenceTextView.text = ""
        }
        val backgroundColor = if (adapterPosition == 0 || adapterPosition % 2 == 0) {
            ContextCompat.getColor(itemView.context, R.color.color_background)
        } else {
            ContextCompat.getColor(itemView.context, R.color.color_fill)
        }
        itemView.backgroundView.setBackgroundColor(backgroundColor)
    }
}
