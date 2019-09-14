package com.ricknout.rugbyranker.prediction.vo

import android.content.Context
import android.os.Parcelable
import com.ricknout.rugbyranker.core.util.EmojiUtils
import com.ricknout.rugbyranker.core.util.FlagUtils
import com.ricknout.rugbyranker.prediction.R
import com.ricknout.rugbyranker.teams.vo.WorldRugbyTeam
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Team(
    val id: Long,
    val name: String,
    val abbreviation: String,
    val title: String
) : Parcelable {

    override fun toString() = title

    fun getEmojiProcessedTitle() = EmojiUtils.processEmoji(title)

    companion object {
        fun from(context: Context, prediction: Prediction, isHomeTeam: Boolean): Team {
            val teamId = if (isHomeTeam) prediction.homeTeamId else prediction.awayTeamId
            val teamName = if (isHomeTeam) prediction.homeTeamName else prediction.awayTeamName
            val teamAbbreviation = if (isHomeTeam) prediction.homeTeamAbbreviation else prediction.awayTeamAbbreviation
            return Team(teamId, teamName, teamAbbreviation, getTitle(context, teamAbbreviation, teamName))
        }
        fun from(context: Context, worldRugbyTeam: WorldRugbyTeam) = Team(
                worldRugbyTeam.id,
                worldRugbyTeam.name,
                worldRugbyTeam.abbreviation,
                getTitle(context, worldRugbyTeam.abbreviation, worldRugbyTeam.name)
        )
        private fun getTitle(context: Context, abbreviation: String, name: String) =
                context.getString(R.string.title_team, FlagUtils.getFlagEmojiForTeamAbbreviation(abbreviation), name)
    }
}
