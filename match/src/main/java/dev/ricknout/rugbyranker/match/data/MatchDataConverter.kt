package dev.ricknout.rugbyranker.match.data

import dev.ricknout.rugbyranker.core.api.Content
import dev.ricknout.rugbyranker.core.api.WorldRugbyMatchSummaryResponse
import dev.ricknout.rugbyranker.core.api.WorldRugbyMatchesResponse
import dev.ricknout.rugbyranker.core.api.WorldRugbyService
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.core.util.DateUtils
import dev.ricknout.rugbyranker.match.model.Half
import dev.ricknout.rugbyranker.match.model.Match
import dev.ricknout.rugbyranker.match.model.Status
import java.lang.IllegalArgumentException

object MatchDataConverter {

    fun getMatchesFromResponse(
        response: WorldRugbyMatchesResponse,
        sport: Sport,
        teamIds: List<Long>
    ): List<Match> {
        return response.content.map { content ->
            val firstTeam = content.teams[0]
            val secondTeam = content.teams[1]
            val event = content.events.firstOrNull()
            Match(
                id = content.matchId,
                sport = sport,
                status = getStatusFromResponse(content),
                description = content.description,
                attendance = content.attendance,
                firstTeamId = firstTeam.id,
                firstTeamName = firstTeam.name,
                firstTeamAbbreviation = firstTeam.abbreviation,
                firstTeamScore = content.scores[0],
                secondTeamId = secondTeam.id,
                secondTeamName = secondTeam.name,
                secondTeamAbbreviation = secondTeam.abbreviation,
                secondTeamScore = content.scores[1],
                timeLabel = content.time.label,
                timeMillis = content.time.millis,
                timeGmtOffset = content.time.gmtOffset.toInt(),
                venueId = content.venue?.id,
                venueName = content.venue?.name,
                venueCity = content.venue?.city,
                venueCountry = content.venue?.country,
                eventId = event?.id,
                eventLabel = event?.label,
                eventRankingsWeight = event?.rankingsWeight,
                eventStartTimeLabel = event?.start?.label,
                eventStartTimeMillis = event?.start?.millis,
                eventStartTimeGmtOffset = event?.start?.gmtOffset?.toInt(),
                eventEndTimeLabel = event?.end?.label,
                eventEndTimeMillis = event?.end?.millis,
                eventEndTimeGmtOffset = event?.end?.gmtOffset?.toInt(),
                predictable = teamIds.containsAll(listOf(firstTeam.id, secondTeam.id)),
                half = getHalfFromResponse(content),
                minute = null
            )
        }
    }

    private fun getStatusFromResponse(content: Content): Status {
        return when (content.status) {
            WorldRugbyService.STATE_UNPLAYED -> Status.UNPLAYED
            WorldRugbyService.STATE_POSTPONED -> Status.POSTPONED
            WorldRugbyService.STATE_COMPLETE -> Status.COMPLETE
            WorldRugbyService.STATE_CANCELLED -> Status.CANCELLED
            WorldRugbyService.STATE_LIVE_1ST_HALF, WorldRugbyService.STATE_LIVE_2ND_HALF, WorldRugbyService.STATE_LIVE_HALF_TIME -> Status.LIVE
            else -> throw IllegalArgumentException("Unknown status ${content.status}")
        }
    }

    private fun getHalfFromResponse(content: Content): Half? {
        return when (content.status) {
            WorldRugbyService.STATE_LIVE_1ST_HALF -> Half.FIRST
            WorldRugbyService.STATE_LIVE_2ND_HALF -> Half.SECOND
            WorldRugbyService.STATE_LIVE_HALF_TIME -> Half.HALF_TIME
            else -> null
        }
    }

    fun getMinuteFromResponse(response: WorldRugbyMatchSummaryResponse): Int? {
        return if (response.match.clock != null) {
            response.match.clock!!.secs / DateUtils.MINUTE_SECS
        } else {
            null
        }
    }
}
