package com.ricknout.rugbyranker.matches.vo

import com.ricknout.rugbyranker.common.api.Match
import com.ricknout.rugbyranker.common.api.WorldRugbyMatchesResponse
import com.ricknout.rugbyranker.common.api.WorldRugbyService
import com.ricknout.rugbyranker.common.vo.Sport
import java.lang.IllegalArgumentException

object MatchesDataConverter {

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
            WorldRugbyService.STATE_LIVE_1ST_HALF, WorldRugbyService.STATE_LIVE_2ND_HALF, WorldRugbyService.STATE_LIVE_HALF_TIME -> MatchStatus.LIVE
            else -> throw IllegalArgumentException("Unknown match status ${match.status}")
        }
    }
}
