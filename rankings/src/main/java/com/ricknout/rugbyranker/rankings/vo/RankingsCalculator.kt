package com.ricknout.rugbyranker.rankings.vo

import com.ricknout.rugbyranker.prediction.vo.MatchPrediction
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object RankingsCalculator {

    fun allocatePointsForMatchPredictions(
        worldRugbyRankings: List<WorldRugbyRanking>,
        matchPredictions: List<MatchPrediction>
    ): List<WorldRugbyRanking> {
        if (matchPredictions.isEmpty()) return worldRugbyRankings
        val mutableWorldRugbyRankings = worldRugbyRankings.asSequence().map { worldRugbyRanking ->
            worldRugbyRanking.resetPreviousPoints() // Reset previous points initially
        }.toMutableList()
        matchPredictions.forEach { matchPrediction ->
            val homeTeam = mutableWorldRugbyRankings.find { worldRugbyRanking ->
                worldRugbyRanking.teamId == matchPrediction.homeTeamId
            } ?: throw IllegalArgumentException("Cannot find home team with ID = ${matchPrediction.homeTeamId}")
            val awayTeam = mutableWorldRugbyRankings.find { worldRugbyRanking ->
                worldRugbyRanking.teamId == matchPrediction.awayTeamId
            } ?: throw IllegalArgumentException("Cannot find away team with ID = ${matchPrediction.awayTeamId}")
            val points = pointsForMatchPrediction(homeTeam, awayTeam, matchPrediction)
            mutableWorldRugbyRankings[mutableWorldRugbyRankings.indexOf(homeTeam)] = homeTeam.addPoints(points)
            mutableWorldRugbyRankings[mutableWorldRugbyRankings.indexOf(awayTeam)] = awayTeam.addPoints(-points)
        }
        return mutableWorldRugbyRankings.asSequence().sortedByDescending { worldRugbyRanking ->
            worldRugbyRanking.points
        }.mapIndexed { index, worldRugbyRanking ->
            val position = index.inc()
            worldRugbyRanking.updatePosition(position)
        }.toList()
    }

    fun pointsForMatchPrediction(
        homeTeam: WorldRugbyRanking,
        awayTeam: WorldRugbyRanking,
        matchPrediction: MatchPrediction
    ): Float {
        // The effective ranking of the home team is an additional 3 points
        val homeTeamPoints = if (!matchPrediction.noHomeAdvantage) homeTeam.points + 3f else homeTeam.points
        // Determine the ranking points difference and clamp to 10 points
        val pointsDifference = min(10f, max(-10f, homeTeamPoints - awayTeam.points))
        // A draw gives the home team one tenth of the ranking points difference
        val drawDifference = pointsDifference / 10f
        // Big/small wins/losses and RWC matches multiply rankings changes
        var multiplier = 1f
        // The points multiplier is 1.5 if either team wins by more than 15 match points
        if (abs(matchPrediction.homeTeamScore - matchPrediction.awayTeamScore) > 15) multiplier *= 1.5f
        // If the match takes place during a Rugby World Cup, the multiplier is doubled
        if (matchPrediction.rugbyWorldCup) multiplier *= 2f
        // Calculate the final (zero-sum) result
        // Take into account that if the home side wins, they gain 1 extra point; if they lose, they gain 1 less point
        return (when {
            matchPrediction.homeTeamScore > matchPrediction.awayTeamScore -> 1f
            matchPrediction.awayTeamScore > matchPrediction.homeTeamScore -> -1f
            else -> 0f
        } - drawDifference) * multiplier
    }
}
