package com.ricknout.rugbyranker.vo

import com.ricknout.rugbyranker.api.WorldRugbyRankingsResponse
import com.ricknout.rugbyranker.common.util.DateUtils

object WorldRugbyRankingDataConverter {

    fun getWorldRugbyRankingsFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse: WorldRugbyRankingsResponse, sport: Sport): List<WorldRugbyRanking> {
        return worldRugbyRankingsResponse.entries.map { entry ->
            WorldRugbyRanking(
                    teamId = entry.team.id,
                    teamName = entry.team.name,
                    teamAbbreviation = entry.team.abbreviation,
                    position = entry.pos,
                    previousPosition = entry.previousPos,
                    points = entry.pts,
                    previousPoints = entry.previousPts,
                    matches = entry.matches,
                    sport = sport
            )
        }
    }

    fun getEffectiveTimeFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse: WorldRugbyRankingsResponse): String {
        return DateUtils.getDate(DateUtils.DATE_FORMAT, worldRugbyRankingsResponse.effective.millis, worldRugbyRankingsResponse.effective.gmtOffset)
    }
}
