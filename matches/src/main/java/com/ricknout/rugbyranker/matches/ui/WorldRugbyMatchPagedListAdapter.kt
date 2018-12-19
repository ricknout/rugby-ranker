package com.ricknout.rugbyranker.matches.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePaddingRelative
import androidx.emoji.text.EmojiCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ricknout.rugbyranker.matches.R
import com.ricknout.rugbyranker.common.util.DateUtils
import com.ricknout.rugbyranker.common.util.FlagUtils
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch
import kotlinx.android.synthetic.main.list_item_world_rugby_match.view.*

class WorldRugbyMatchPagedListAdapter(
    private val onItemCountChange: () -> Unit,
    private val onPredictClick: (worldRugbyMatch: WorldRugbyMatch) -> Unit
) : PagedListAdapter<WorldRugbyMatch, WorldRugbyMatchViewHolder>(DIFF_CALLBACK) {

    private var itemCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WorldRugbyMatchViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_world_rugby_match, parent, false))

    override fun onBindViewHolder(holder: WorldRugbyMatchViewHolder, position: Int) {
        val worldRugbyMatch = getItem(position) ?: return
        holder.bind(worldRugbyMatch, onPredictClick)
    }

    override fun getItemCount(): Int {
        val itemCount = super.getItemCount()
        if (this.itemCount != itemCount) {
            onItemCountChange.invoke()
            this.itemCount = itemCount
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

class WorldRugbyMatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(worldRugbyMatch: WorldRugbyMatch, onPredictClick: (worldRugbyMatch: WorldRugbyMatch) -> Unit) {
        val showScores = worldRugbyMatch.status == MatchStatus.COMPLETE
        val showTime = worldRugbyMatch.status == MatchStatus.UNPLAYED
        val showPredict = worldRugbyMatch.status == MatchStatus.UNPLAYED
        val firstTeamFlag = EmojiCompat.get().process(FlagUtils.getFlagEmojiForTeamAbbreviation(worldRugbyMatch.firstTeamAbbreviation ?: ""))
        val secondTeamFlag = EmojiCompat.get().process(FlagUtils.getFlagEmojiForTeamAbbreviation(worldRugbyMatch.secondTeamAbbreviation ?: ""))
        itemView.team1FlagTextView.text = firstTeamFlag
        itemView.team1TextView.text = worldRugbyMatch.firstTeamName
        itemView.team2FlagTextView.text = secondTeamFlag
        itemView.team2TextView.text = worldRugbyMatch.secondTeamName
        if (showScores) {
            itemView.team1ScoreTextView.isVisible = true
            itemView.team2ScoreTextView.isVisible = true
            itemView.team1ScoreTextView.text = worldRugbyMatch.firstTeamScore.toString()
            itemView.team2ScoreTextView.text = worldRugbyMatch.secondTeamScore.toString()
        } else {
            itemView.team1ScoreTextView.isVisible = false
            itemView.team2ScoreTextView.isVisible = false
        }
        if (showTime) {
            val time = DateUtils.getDate(DateUtils.DATE_FORMAT_HH_MM, worldRugbyMatch.timeMillis)
            itemView.timeTextView.text = time
            itemView.timeTextView.isVisible = true
        } else {
            itemView.timeTextView.text = null
            itemView.timeTextView.isVisible = false
        }
        itemView.eventTextView.text = worldRugbyMatch.eventLabel
        itemView.eventTextView.isVisible = worldRugbyMatch.eventLabel != null
        itemView.venueTextView.text = when {
            worldRugbyMatch.venueName != null && worldRugbyMatch.venueCountry != null -> {
                itemView.context.getString(R.string.text_match_venue_country, worldRugbyMatch.venueName, worldRugbyMatch.venueCountry)
            }
            worldRugbyMatch.venueName != null -> worldRugbyMatch.venueName
            worldRugbyMatch.venueCountry != null -> worldRugbyMatch.venueCountry
            else -> null
        }
        itemView.venueTextView.isVisible = worldRugbyMatch.venueName != null && worldRugbyMatch.venueCountry != null
        if (showPredict) {
            itemView.predictButton.setOnClickListener {
                onPredictClick.invoke(worldRugbyMatch)
            }
            itemView.predictButton.isVisible = true
            itemView.constraintLayout.updatePaddingRelative(bottom = 0)
        } else {
            itemView.predictButton.setOnClickListener(null)
            itemView.predictButton.isVisible = false
            itemView.constraintLayout.updatePaddingRelative(bottom = itemView.context.resources.getDimensionPixelSize(R.dimen.spacing_double))
        }
    }
}
