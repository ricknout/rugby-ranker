package com.ricknout.rugbyranker.vo

import com.ricknout.rugbyranker.api.WorldRugbyRankingsResponse

object WorldRugbyRankingDataConverter {

    fun getWorldRugbyRankingsFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse: WorldRugbyRankingsResponse, rankingsType: RankingsType): List<WorldRugbyRanking> {
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
                    rankingsType = rankingsType
            )
        }
    }

    fun getEffectiveTimeFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse: WorldRugbyRankingsResponse): String {
        return worldRugbyRankingsResponse.effective.label
    }
}
