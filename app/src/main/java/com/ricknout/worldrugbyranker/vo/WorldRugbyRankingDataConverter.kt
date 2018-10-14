package com.ricknout.worldrugbyranker.vo

import com.ricknout.worldrugbyranker.api.WorldRugbyRankingsResponse

object WorldRugbyRankingDataConverter {

    fun convertFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse: WorldRugbyRankingsResponse, rankingsType: RankingsType): List<WorldRugbyRanking> {
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
}
