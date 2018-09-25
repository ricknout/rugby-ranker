package com.ricknout.worldrugbyranker.vo

import com.ricknout.worldrugbyranker.api.WorldRugbyRankingsResponse

object WorldRugbyRankingDataConverter {

    fun convertFromMensWorldRugbyRankingsResponse(worldRugbyRankingsResponse: WorldRugbyRankingsResponse): List<MensWorldRugbyRanking> {
        return worldRugbyRankingsResponse.entries.map { entry ->
            MensWorldRugbyRanking(
                    teamId = entry.team.id,
                    teamName = entry.team.name,
                    teamAbbreviation = entry.team.abbreviation,
                    position = entry.pos,
                    previousPosition = entry.previousPos,
                    points = entry.pts,
                    previousPoints = entry.previousPts,
                    matches = entry.matches
            )
        }
    }

    fun convertFromWomensWorldRugbyRankingsResponse(worldRugbyRankingsResponse: WorldRugbyRankingsResponse): List<WomensWorldRugbyRanking> {
        return worldRugbyRankingsResponse.entries.map { entry ->
            WomensWorldRugbyRanking(
                    teamId = entry.team.id,
                    teamName = entry.team.name,
                    teamAbbreviation = entry.team.abbreviation,
                    position = entry.pos,
                    previousPosition = entry.previousPos,
                    points = entry.pts,
                    previousPoints = entry.previousPts,
                    matches = entry.matches
            )
        }
    }
}