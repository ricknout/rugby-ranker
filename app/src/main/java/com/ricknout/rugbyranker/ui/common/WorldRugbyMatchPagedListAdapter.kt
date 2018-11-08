package com.ricknout.rugbyranker.ui.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ricknout.rugbyranker.R
import com.ricknout.rugbyranker.common.util.DateUtils
import com.ricknout.rugbyranker.util.FlagUtils
import com.ricknout.rugbyranker.vo.WorldRugbyMatch
import kotlinx.android.synthetic.main.list_item_world_rugby_match.view.*

class WorldRugbyMatchPagedListAdapter(
        private val showScores: Boolean
) : PagedListAdapter<WorldRugbyMatch, WorldRugbyMatchViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = WorldRugbyMatchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_world_rugby_match, parent, false))

    override fun onBindViewHolder(holder: WorldRugbyMatchViewHolder, position: Int) {
        val worldRugbyMatch = getItem(position) ?: return
        holder.bind(worldRugbyMatch, showScores)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WorldRugbyMatch>() {
            override fun areItemsTheSame(oldItem: WorldRugbyMatch, newItem: WorldRugbyMatch) = oldItem.matchId == newItem.matchId
            override fun areContentsTheSame(oldItem: WorldRugbyMatch, newItem: WorldRugbyMatch) = oldItem == newItem
        }
    }
}

class WorldRugbyMatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(worldRugbyMatch: WorldRugbyMatch, showScores: Boolean) {
        val date = DateUtils.getDate(DateUtils.DATE_FORMAT_YYYY_MM_DD, worldRugbyMatch.timeMillis)
        itemView.dateTextView.text = date
        val time = DateUtils.getDate(DateUtils.DATE_FORMAT_HH_MM, worldRugbyMatch.timeMillis)
        itemView.timeTextView.text = time
        val firstTeamFlag = FlagUtils.getFlagEmojiForTeamAbbreviation(worldRugbyMatch.firstTeamAbbreviation ?: "")
        val secondTeamFlag = FlagUtils.getFlagEmojiForTeamAbbreviation(worldRugbyMatch.secondTeamAbbreviation ?: "")
        val teams = if (showScores) {
            itemView.context.getString(R.string.text_match_teams_with_scores,
                    firstTeamFlag, worldRugbyMatch.firstTeamName, worldRugbyMatch.firstTeamScore, worldRugbyMatch.secondTeamScore, worldRugbyMatch.secondTeamName, secondTeamFlag)
        } else {
            itemView.context.getString(R.string.text_match_teams,
                    firstTeamFlag, worldRugbyMatch.firstTeamName, worldRugbyMatch.secondTeamName, secondTeamFlag)
        }
        itemView.teamsTextView.text = teams
        itemView.eventTextView.text = worldRugbyMatch.eventLabel
        itemView.eventTextView.isVisible = worldRugbyMatch.eventLabel != null
        itemView.venueTextView.text = worldRugbyMatch.venueName
        itemView.venueTextView.isVisible = worldRugbyMatch.venueName != null
    }
}
