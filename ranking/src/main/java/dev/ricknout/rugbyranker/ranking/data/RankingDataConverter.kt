package dev.ricknout.rugbyranker.ranking.data

import dev.ricknout.rugbyranker.core.api.WorldRugbyRankingsResponse
import dev.ricknout.rugbyranker.core.model.Ranking
import dev.ricknout.rugbyranker.core.model.Sport

object RankingDataConverter {

    fun getRankingsFromResponse(response: WorldRugbyRankingsResponse, sport: Sport): List<Ranking> {
        return response.entries.map { entry ->
            Ranking(
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
