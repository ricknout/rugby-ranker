package dev.ricknout.rugbyranker.match.ui

import android.content.res.ColorStateList
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import dev.ricknout.rugbyranker.core.util.DateUtils
import dev.ricknout.rugbyranker.core.util.FlagUtils
import dev.ricknout.rugbyranker.match.R
import dev.ricknout.rugbyranker.match.databinding.ListItemMatchBinding
import dev.ricknout.rugbyranker.match.model.Half
import dev.ricknout.rugbyranker.match.model.Match
import dev.ricknout.rugbyranker.match.model.Status
import kotlin.math.floor

class MatchViewHolder(private val binding: ListItemMatchBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        match: Match,
        onPredictClick: (match: Match) -> Unit,
        onPinClick: (match: Match) -> Unit,
    ) {
        binding.apply {
            homeFlag.text = FlagUtils.getFlagEmojiForTeamAbbreviation(match.firstTeamAbbreviation)
            awayFlag.text = FlagUtils.getFlagEmojiForTeamAbbreviation(match.secondTeamAbbreviation)
            homeName.text = match.firstTeamName
            awayName.text = match.secondTeamName
            val isCurrentDay = DateUtils.isDayCurrentDay(match.timeMillis)
            val highEmphasisColor = ColorStateList.valueOf(MaterialColors.getColor(root, R.attr.colorOnSurface))
            val mediumEmphasisColor = AppCompatResources.getColorStateList(root.context, R.color.material_on_surface_emphasis_medium)
            when (match.status) {
                Status.UNPLAYED -> {
                    title.text =
                        if (isCurrentDay) {
                            root.context.getString(R.string.today)
                        } else {
                            DateUtils.getDate(DateUtils.DATE_FORMAT_E_D_MMM_YYYY, match.timeMillis)
                        }
                    label.text = DateUtils.getDate(DateUtils.DATE_FORMAT_HH_MM, match.timeMillis)
                    homeScoreText.setTextColor(mediumEmphasisColor)
                    homeScoreText.text = root.context.getString(R.string.versus)
                    awayScore.isVisible = false
                    awayScoreText.text = null
                }
                Status.COMPLETE -> {
                    title.text =
                        if (isCurrentDay) {
                            root.context.getString(R.string.today)
                        } else {
                            DateUtils.getDate(DateUtils.DATE_FORMAT_E_D_MMM_YYYY, match.timeMillis)
                        }
                    label.text = null
                    homeScoreText.setTextColor(highEmphasisColor)
                    homeScoreText.text = match.firstTeamScore.toString()
                    awayScore.isVisible = true
                    awayScoreText.text = match.secondTeamScore.toString()
                }
                Status.LIVE -> {
                    title.text = root.context.getString(R.string.live)
                    val half =
                        when (match.half) {
                            Half.FIRST -> root.context.getString(R.string.first_half)
                            Half.SECOND -> root.context.getString(R.string.second_half)
                            Half.HALF_TIME -> root.context.getString(R.string.half_time)
                            else -> null
                        }
                    label.text =
                        if (match.minute != null && half != null) {
                            root.context.getString(R.string.half_minute, half, match.minute)
                        } else {
                            null
                        }
                    homeScoreText.setTextColor(highEmphasisColor)
                    homeScoreText.text = match.firstTeamScore.toString()
                    awayScore.isVisible = true
                    awayScoreText.text = match.secondTeamScore.toString()
                }
                Status.POSTPONED -> {
                    title.text =
                        if (isCurrentDay) {
                            root.context.getString(R.string.today)
                        } else {
                            DateUtils.getDate(DateUtils.DATE_FORMAT_E_D_MMM_YYYY, match.timeMillis)
                        }
                    label.text = root.context.getString(R.string.postponed)
                    homeScoreText.setTextColor(mediumEmphasisColor)
                    homeScoreText.text = root.context.getString(R.string.versus)
                    awayScore.isVisible = false
                    awayScoreText.text = null
                }
                Status.CANCELLED -> {
                    title.text =
                        if (isCurrentDay) {
                            root.context.getString(R.string.today)
                        } else {
                            DateUtils.getDate(DateUtils.DATE_FORMAT_E_D_MMM_YYYY, match.timeMillis)
                        }
                    label.text = root.context.getString(R.string.cancelled)
                    homeScoreText.setTextColor(highEmphasisColor)
                    homeScoreText.text = match.firstTeamScore.toString()
                    awayScore.isVisible = true
                    awayScoreText.text = match.secondTeamScore.toString()
                }
            }
            event.isVisible = match.eventLabel != null
            eventText.text = match.eventLabel
            place.isVisible = match.venueName != null || match.venueCity != null || match.venueCountry != null
            placeText.text =
                when {
                    match.venueName != null && match.venueCity != null && match.venueCountry != null -> {
                        root.context.getString(R.string.venue_3, match.venueName, match.venueCity, match.venueCountry)
                    }
                    match.venueName != null && match.venueCity != null -> {
                        root.context.getString(R.string.venue_2, match.venueName, match.venueCity)
                    }
                    match.venueName != null && match.venueCountry != null -> {
                        root.context.getString(R.string.venue_2, match.venueName, match.venueCountry)
                    }
                    match.venueCity != null && match.venueCountry != null -> {
                        root.context.getString(R.string.venue_2, match.venueCity, match.venueCountry)
                    }
                    match.venueName != null -> match.venueName
                    match.venueCity != null -> match.venueCity
                    match.venueCountry != null -> match.venueCountry
                    else -> null
                }
            when {
                match.predictable &&
                    (match.status == Status.UNPLAYED || match.status == Status.LIVE || match.status == Status.POSTPONED) -> {
                    predict.isVisible = true
                    predictButton.setOnClickListener { onPredictClick(match) }
                }
                else -> {
                    predict.isVisible = false
                    predictButton.setOnClickListener(null)
                }
            }
            when (match.status) {
                Status.LIVE -> {
                    pinButton.isVisible = true
                    pinButton.setOnClickListener { onPinClick(match) }
                }
                else -> {
                    pinButton.isVisible = false
                    pinButton.setOnClickListener(null)
                }
            }
            val spanCount = itemView.context.resources.getInteger(R.integer.span_count_grid)
            val row = floor(absoluteAdapterPosition / spanCount.toFloat()).toInt()
            val backgroundColor =
                when {
                    (spanCount % 2 != 0 || row % 2 == 0) && absoluteAdapterPosition % 2 == 0 -> {
                        AppCompatResources.getColorStateList(itemView.context, R.color.on_surface_5).defaultColor
                    }
                    (spanCount % 2 == 0 && row % 2 != 0) && absoluteAdapterPosition % 2 != 0 -> {
                        AppCompatResources.getColorStateList(itemView.context, R.color.on_surface_5).defaultColor
                    }
                    else -> MaterialColors.getColor(root, R.attr.colorSurface)
                }
            root.setBackgroundColor(backgroundColor)
        }
    }
}
