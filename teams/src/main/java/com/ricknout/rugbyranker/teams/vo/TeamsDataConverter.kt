package com.ricknout.rugbyranker.teams.vo

import com.ricknout.rugbyranker.core.api.WorldRugbyRankingsResponse
import com.ricknout.rugbyranker.core.vo.Sport

object TeamsDataConverter {

    fun getWorldRugbyTeamsFromWorldRugbyRankingsResponse(worldRugbyRankingsResponse: WorldRugbyRankingsResponse, sport: Sport): List<WorldRugbyTeam> {
        return worldRugbyRankingsResponse.entries.map { entry ->
            WorldRugbyTeam(
                    id = entry.team.id,
                    name = entry.team.name,
                    abbreviation = entry.team.abbreviation!!,
                    sport = sport
            )
        }
    }
}
