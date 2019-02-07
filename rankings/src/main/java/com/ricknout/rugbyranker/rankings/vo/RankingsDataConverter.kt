package com.ricknout.rugbyranker.rankings.vo

import com.ricknout.rugbyranker.core.api.WorldRugbyRankingsResponse
import com.ricknout.rugbyranker.core.vo.Sport

object RankingsDataConverter {

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
}
