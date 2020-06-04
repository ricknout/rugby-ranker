package dev.ricknout.rugbyranker.ranking.ui

import android.content.res.ColorStateList
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import dev.ricknout.rugbyranker.core.model.Ranking
import dev.ricknout.rugbyranker.core.util.FlagUtils
import dev.ricknout.rugbyranker.ranking.R
import dev.ricknout.rugbyranker.ranking.databinding.ListItemRankingBinding
import kotlin.math.abs

class RankingViewHolder(private val binding: ListItemRankingBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(ranking: Ranking) {
        binding.apply {
            position.text = root.context.getString(R.string.position, ranking.position)
            previousPosition.text = root.context.getString(R.string.previous_position, ranking.previousPosition)
            points.text = root.context.getString(R.string.points, ranking.points)
            pointsDifference.text = root.context.getString(R.string.points_difference, abs(ranking.pointsDifference()))
            flag.text = FlagUtils.getFlagEmojiForTeamAbbreviation(ranking.teamAbbreviation)
            team.text = if (root.context.resources.getBoolean(R.bool.full_team_name)) ranking.teamName else ranking.teamAbbreviation
            TooltipCompat.setTooltipText(team, ranking.teamName)
            val positiveColor = ColorStateList.valueOf(MaterialColors.getColor(root, R.attr.colorPrimary))
            val negativeColor = ColorStateList.valueOf(MaterialColors.getColor(root, R.attr.colorError))
            val mediumNeutralColor = AppCompatResources.getColorStateList(root.context, R.color.material_on_surface_emphasis_medium)
            val disabledNeutralColor = AppCompatResources.getColorStateList(root.context, R.color.material_on_surface_disabled)
            when {
                ranking.position < ranking.previousPosition -> {
                    positionIcon.setImageResource(R.drawable.ic_arrow_upward_24)
                    positionIcon.imageTintList = positiveColor
                    previousPosition.setTextColor(mediumNeutralColor)
                }
                ranking.position > ranking.previousPosition -> {
                    positionIcon.setImageResource(R.drawable.ic_arrow_downward_24dp)
                    positionIcon.imageTintList = negativeColor
                    previousPosition.setTextColor(mediumNeutralColor)
                }
                else -> {
                    positionIcon.setImageResource(R.drawable.ic_arrow_forward_24dp)
                    positionIcon.imageTintList = disabledNeutralColor
                    previousPosition.setTextColor(disabledNeutralColor)
                }
            }
            when {
                ranking.points > ranking.previousPoints -> {
                    pointsIcon.setImageResource(R.drawable.ic_add_24dp)
                    pointsIcon.imageTintList = positiveColor
                    pointsIcon.isInvisible = false
                    pointsDifference.isInvisible = false
                }
                ranking.points < ranking.previousPoints -> {
                    pointsIcon.setImageResource(R.drawable.ic_remove_24dp)
                    pointsIcon.imageTintList = negativeColor
                    pointsIcon.isInvisible = false
                    pointsDifference.isInvisible = false
                }
                else -> {
                    pointsIcon.isInvisible = true
                    pointsDifference.isInvisible = true
                }
            }
            val backgroundColor = if (absoluteAdapterPosition % 2 == 0) {
                AppCompatResources.getColorStateList(itemView.context, R.color.on_surface_5).defaultColor
            } else {
                MaterialColors.getColor(root, R.attr.colorSurface)
            }
            root.setBackgroundColor(backgroundColor)
        }
    }
}
