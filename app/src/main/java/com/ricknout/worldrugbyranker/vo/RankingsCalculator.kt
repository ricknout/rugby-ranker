package com.ricknout.worldrugbyranker.vo

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

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
        val homeTeamPoints = if (!matchResult.noHomeAdvantage) homeTeam.points + 3f else homeTeam.points
        val pointsDifference = min(10f, max(-10f, homeTeamPoints - awayTeam.points))
        val drawDifference = pointsDifference / 10f
        var multiplier = 1f
        if (abs(matchResult.homeTeamScore - matchResult.awayTeamScore) > 15) multiplier *= 1.5f
        if (matchResult.rugbyWorldCup) multiplier *= 2f
        return (when {
            matchResult.homeTeamScore > matchResult.awayTeamScore -> 1f
            matchResult.awayTeamScore > matchResult.homeTeamScore -> -1f
            else -> 0f
        } - drawDifference) * multiplier
    }
}