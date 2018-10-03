package com.ricknout.worldrugbyranker.vo

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object RankingsCalculator {

    fun allocatePointsForMatchResults(
            worldRugbyRankings: List<WorldRugbyRanking>,
            matchResults: List<MatchResult>
    ): List<WorldRugbyRanking> {
        if (matchResults.isEmpty()) return worldRugbyRankings
        val mutableWorldRugbyRankings = worldRugbyRankings.map { worldRugbyRanking ->
            worldRugbyRanking.allocatePoints(0f) // Reset previous points initially
        }.toMutableList()
        matchResults.forEach { matchResult ->
            val homeTeam = worldRugbyRankings.find { worldRugbyRanking ->
                worldRugbyRanking.teamId == matchResult.homeTeamId
            } ?: throw IllegalArgumentException("Cannot find home team with ID = ${matchResult.homeTeamId}")
            val awayTeam = worldRugbyRankings.find { worldRugbyRanking ->
                worldRugbyRanking.teamId == matchResult.awayTeamId
            } ?: throw IllegalArgumentException("Cannot find away team with ID = ${matchResult.awayTeamId}")
            val points = pointsForMatchResult(homeTeam, awayTeam, matchResult)
            mutableWorldRugbyRankings[worldRugbyRankings.indexOf(homeTeam)] = homeTeam.allocatePoints(points)
            mutableWorldRugbyRankings[worldRugbyRankings.indexOf(awayTeam)] = awayTeam.allocatePoints(-points)
        }
        return mutableWorldRugbyRankings.asSequence().sortedByDescending { worldRugbyRanking ->
            worldRugbyRanking.points
        }.mapIndexed { index, worldRugbyRanking ->
            val position = index.inc()
            worldRugbyRanking.updatePosition(position)
        }.toList()
    }

    private fun pointsForMatchResult(
            homeTeam: WorldRugbyRanking,
            awayTeam: WorldRugbyRanking,
            matchResult: MatchResult
    ): Float {
        // The effective ranking of the home team is an additional 3 points
        val homeTeamPoints = if (!matchResult.noHomeAdvantage) homeTeam.points + 3f else homeTeam.points
        // Determine the ranking points difference and clamp to 10 points
        val pointsDifference = min(10f, max(-10f, homeTeamPoints - awayTeam.points))
        // A draw gives the home team one tenth of the ranking points difference
        val drawDifference = pointsDifference / 10f
        // Big/small wins/losses and RWC matches multiply rankings changes
        var multiplier = 1f
        // The points multiplier is 1.5 if either team wins by more than 15 match points
        if (abs(matchResult.homeTeamScore - matchResult.awayTeamScore) > 15) multiplier *= 1.5f
        // If the match takes place during a Rugby World Cup, the multiplier is doubled
        if (matchResult.rugbyWorldCup) multiplier *= 2f
        // Calculate the final (zero-sum) result
        // Take into account that if the home side wins, they gain 1 extra point; if they lose, they gain 1 less point
        return (when {
            matchResult.homeTeamScore > matchResult.awayTeamScore -> 1f
            matchResult.awayTeamScore > matchResult.homeTeamScore -> -1f
            else -> 0f
        } - drawDifference) * multiplier
    }
}
