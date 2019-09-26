package com.ricknout.rugbyranker.matches.vo

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.ricknout.rugbyranker.core.vo.Sport

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
    val eventEndTimeGmtOffset: Int?,
    @Ignore
    val half: MatchHalf?,
    @Ignore
    val minute: Int?
) {

    constructor(
        matchId: Long,
        description: String?,
        status: MatchStatus,
        attendance: Int,
        firstTeamId: Long,
        firstTeamName: String,
        firstTeamAbbreviation: String?,
        firstTeamScore: Int,
        secondTeamId: Long,
        secondTeamName: String,
        secondTeamAbbreviation: String?,
        secondTeamScore: Int,
        timeLabel: String,
        timeMillis: Long,
        timeGmtOffset: Int,
        venueId: Long?,
        venueName: String?,
        venueCity: String?,
        venueCountry: String?,
        eventId: Long?,
        eventLabel: String?,
        eventSport: Sport,
        eventRankingsWeight: Float?,
        eventStartTimeLabel: String?,
        eventStartTimeMillis: Long?,
        eventStartTimeGmtOffset: Int?,
        eventEndTimeLabel: String?,
        eventEndTimeMillis: Long?,
        eventEndTimeGmtOffset: Int?
    ) : this(matchId, description, status, attendance,
            firstTeamId, firstTeamName, firstTeamAbbreviation, firstTeamScore,
            secondTeamId, secondTeamName, secondTeamAbbreviation, secondTeamScore,
            timeLabel, timeMillis, timeGmtOffset, venueId, venueName, venueCity, venueCountry,
            eventId, eventLabel, eventSport, eventRankingsWeight,
            eventStartTimeLabel, eventStartTimeMillis, eventStartTimeGmtOffset,
            eventEndTimeLabel, eventEndTimeMillis, eventEndTimeGmtOffset,
            half = null, minute = null
    )
}
