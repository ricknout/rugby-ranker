package com.ricknout.rugbyranker.vo

import com.ricknout.rugbyranker.api.Match
import com.ricknout.rugbyranker.api.WorldRugbyMatchesResponse
import com.ricknout.rugbyranker.api.WorldRugbyRankingsResponse
import com.ricknout.rugbyranker.api.WorldRugbyService
import com.ricknout.rugbyranker.common.vo.Sport
import java.lang.IllegalArgumentException

object WorldRugbyDataConverter {

    fun getWorldRugbyRankingsFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse: WorldRugbyRankingsResponse, sport: Sport): List<WorldRugbyRanking> {
        return worldRugbyRankingsResponse.entries.map { entry ->
            WorldRugbyRanking(
                    teamId = entry.team.id,
                    teamName = entry.team.name,
                    teamAbbreviation = entry.team.abbreviation!!,
                    position = entry.pos,
                    previousPosition = entry.previousPos,
                    points = entry.pts,
                    previousPoints = entry.previousPts,
                    matches = entry.matches,
                    sport = sport
            )
        }
    }

    fun getWorldRugbyMatchesFromWorldRugbyMatchesResponse(worldRugbyMatchesResponse: WorldRugbyMatchesResponse, sport: Sport): List<WorldRugbyMatch> {
        return worldRugbyMatchesResponse.content.map { match ->
            WorldRugbyMatch(
                    matchId = match.matchId,
                    description = match.description,
                    status = getMatchStatusFromMatch(match),
                    attendance = match.attendance,
                    firstTeamId = match.teams[0].id,
                    firstTeamName = match.teams[0].name,
                    firstTeamAbbreviation = match.teams[0].abbreviation,
                    firstTeamScore = match.scores[0],
                    secondTeamId = match.teams[1].id,
                    secondTeamName = match.teams[1].name,
                    secondTeamAbbreviation = match.teams[1].abbreviation,
                    secondTeamScore = match.scores[1],
                    timeLabel = match.time.label,
                    timeMillis = match.time.millis,
                    timeGmtOffset = match.time.gmtOffset.toInt(),
                    venueId = match.venue?.id,
                    venueName = match.venue?.name,
                    venueCity = match.venue?.city,
                    venueCountry = match.venue?.country,
                    eventId = match.events.firstOrNull()?.id,
                    eventLabel = match.events.firstOrNull()?.label,
                    eventSport = sport,
                    eventRankingsWeight = match.events.firstOrNull()?.rankingsWeight,
                    eventStartTimeLabel = match.events.firstOrNull()?.start?.label,
                    eventStartTimeMillis = match.events.firstOrNull()?.start?.millis,
                    eventStartTimeGmtOffset = match.events.firstOrNull()?.start?.gmtOffset?.toInt(),
                    eventEndTimeLabel = match.events.firstOrNull()?.end?.label,
                    eventEndTimeMillis = match.events.firstOrNull()?.end?.millis,
                    eventEndTimeGmtOffset = match.events.firstOrNull()?.end?.gmtOffset?.toInt()
            )
        }
    }

    private fun getMatchStatusFromMatch(match: Match): MatchStatus {
        return when (match.status) {
            WorldRugbyService.STATE_UNPLAYED -> MatchStatus.UNPLAYED
            WorldRugbyService.STATE_COMPLETE -> MatchStatus.COMPLETE
            else -> throw IllegalArgumentException("Unknown match status ${match.status}")
        }
    }
}
