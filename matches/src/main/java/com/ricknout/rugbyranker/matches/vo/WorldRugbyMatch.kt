package com.ricknout.rugbyranker.matches.vo

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.ricknout.rugbyranker.common.vo.Sport

@Entity(tableName = "world_rugby_matches")
data class WorldRugbyMatch(
    @PrimaryKey
    val matchId: Long,
    val description: String?,
    val status: MatchStatus,
    val attendance: Int,
    val firstTeamId: Long,
    val firstTeamName: String,
    val firstTeamAbbreviation: String?,
    val firstTeamScore: Int,
    val secondTeamId: Long,
    val secondTeamName: String,
    val secondTeamAbbreviation: String?,
    val secondTeamScore: Int,
    val timeLabel: String,
    val timeMillis: Long,
    val timeGmtOffset: Int,
    val venueId: Long?,
    val venueName: String?,
    val venueCity: String?,
    val venueCountry: String?,
    val eventId: Long?,
    val eventLabel: String?,
    val eventSport: Sport,
    val eventRankingsWeight: Float?,
    val eventStartTimeLabel: String?,
    val eventStartTimeMillis: Long?,
    val eventStartTimeGmtOffset: Int?,
    val eventEndTimeLabel: String?,
    val eventEndTimeMillis: Long?,
    val eventEndTimeGmtOffset: Int?
) {
    @Ignore
    var half: MatchHalf? = null
}
