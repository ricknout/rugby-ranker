package com.ricknout.worldrugbyranker.vo

object RankingsCalculator {

    fun allocatePointsForMatchResult(
            worldRugbyRankings: List<WorldRugbyRanking>,
            matchResult: MatchResult
    ): List<WorldRugbyRanking> {
        val homeTeam = worldRugbyRankings.find { worldRugbyRanking ->
            worldRugbyRanking.teamId == matchResult.homeTeamId
        } ?: throw IllegalArgumentException("Cannot find home team with ID = ${matchResult.homeTeamId}")
        val awayTeam = worldRugbyRankings.find { worldRugbyRanking ->
            worldRugbyRanking.teamId == matchResult.awayTeamId
        } ?: throw IllegalArgumentException("Cannot find away team with ID = ${matchResult.awayTeamId}")
        val points = pointsForMatchResult(homeTeam, awayTeam, matchResult)
        return worldRugbyRankings.map { worldRugbyRanking ->
            when (worldRugbyRanking.teamId) {
                matchResult.homeTeamId -> worldRugbyRanking.allocatePoints(points)
                matchResult.awayTeamId -> worldRugbyRanking.allocatePoints(-points)
                else -> worldRugbyRanking
            }
        }.sortedByDescending { worldRugbyRanking ->
            worldRugbyRanking.points
        }.mapIndexed { index, worldRugbyRanking ->
            val position = index.inc()
            worldRugbyRanking.updatePosition(position)
        }
    }

    private fun pointsForMatchResult(
            homeTeam: WorldRugbyRanking,
            awayTeam: WorldRugbyRanking,
            matchResult: MatchResult
    ): Float {
        return 1f // TODO: Implement actual World Rugby formula
    }
}