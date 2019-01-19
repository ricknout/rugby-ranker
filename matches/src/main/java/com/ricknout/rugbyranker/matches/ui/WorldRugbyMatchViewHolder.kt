package com.ricknout.rugbyranker.matches.ui

import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.updatePaddingRelative
import androidx.emoji.text.EmojiCompat
import androidx.recyclerview.widget.RecyclerView
import com.ricknout.rugbyranker.common.util.DateUtils
import com.ricknout.rugbyranker.common.util.FlagUtils
import com.ricknout.rugbyranker.matches.R
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch
import kotlinx.android.synthetic.main.list_item_world_rugby_match.view.*

class WorldRugbyMatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(worldRugbyMatch: WorldRugbyMatch, onPredictClick: (worldRugbyMatch: WorldRugbyMatch) -> Unit) {
        val showScores = worldRugbyMatch.status == MatchStatus.COMPLETE || worldRugbyMatch.status == MatchStatus.LIVE
        val showTime = worldRugbyMatch.status == MatchStatus.UNPLAYED || worldRugbyMatch.status == MatchStatus.LIVE
        val showPredict = worldRugbyMatch.status == MatchStatus.UNPLAYED || worldRugbyMatch.status == MatchStatus.LIVE
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
