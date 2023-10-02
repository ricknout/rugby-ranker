package dev.ricknout.rugbyranker.match.model

import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.prediction.model.Prediction
import dev.ricknout.rugbyranker.prediction.model.Team

data class Match(
    val id: String,
    val sport: Sport,
    val status: Status,
    val description: String?,
    val attendance: Int,
    val firstTeamId: String,
    val firstTeamName: String,
    val firstTeamAbbreviation: String?,
    val firstTeamScore: Int,
    val secondTeamId: String,
    val secondTeamName: String,
    val secondTeamAbbreviation: String?,
    val secondTeamScore: Int,
    val timeLabel: String,
    val timeMillis: Long,
    val timeGmtOffset: Int,
    val venueId: String?,
    val venueName: String?,
    val venueCity: String?,
    val venueCountry: String?,
    val eventId: String?,
    val eventLabel: String?,
    val eventRankingsWeight: Float?,
    val eventStartTimeLabel: String?,
    val eventStartTimeMillis: Long?,
    val eventStartTimeGmtOffset: Int?,
    val eventEndTimeLabel: String?,
    val eventEndTimeMillis: Long?,
    val eventEndTimeGmtOffset: Int?,
    val numberOfRankedTeams: Int,
    val half: Half?,
    val minute: Int?,
) {
    val predictable: Boolean
        get() = numberOfRankedTeams == 2

    fun toPrediction(): Prediction {
        val switched = secondTeamName == venueCountry
        return Prediction(
            id = id,
            homeTeam = Team(
                id = if (switched) secondTeamId else firstTeamId,
                name = if (switched) secondTeamName else firstTeamName,
                abbreviation = if (switched) secondTeamAbbreviation!! else firstTeamAbbreviation!!,
            ),
            awayTeam = Team(
                id = if (switched) firstTeamId else secondTeamId,
                name = if (switched) firstTeamName else secondTeamName,
                abbreviation = if (switched) firstTeamAbbreviation!! else secondTeamAbbreviation!!,
            ),
            homeScore = when {
                status == Status.UNPLAYED || status == Status.POSTPONED -> 0
                switched -> secondTeamScore
                else -> firstTeamScore
            },
            awayScore = when {
                status == Status.UNPLAYED || status == Status.POSTPONED -> 0
                switched -> firstTeamScore
                else -> secondTeamScore
            },
            rugbyWorldCup = eventLabel?.let { eventLabel ->
                eventLabel.contains("Rugby World Cup", ignoreCase = true) && !eventLabel.contains("Qualifying", ignoreCase = true)
            } ?: false,
            noHomeAdvantage = venueCountry?.let { venueCountry ->
                venueCountry != firstTeamName && venueCountry != secondTeamName
            } ?: false,
        )
    }
}
