package com.ricknout.rugbyranker.rankings.vo

import androidx.annotation.VisibleForTesting
import com.ricknout.rugbyranker.prediction.vo.Prediction
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object RankingsCalculator {

    fun allocatePointsForPredictions(
        worldRugbyRankings: List<WorldRugbyRanking>,
        predictions: List<Prediction>
    ): List<WorldRugbyRanking> {
        if (predictions.isEmpty()) return worldRugbyRankings
        val mutableWorldRugbyRankings = worldRugbyRankings.asSequence().map { worldRugbyRanking ->
            worldRugbyRanking.resetPreviousPoints() // Reset previous points initially
        }.toMutableList()
        predictions.forEach { prediction ->
            val homeTeam = mutableWorldRugbyRankings.find { worldRugbyRanking ->
                worldRugbyRanking.teamId == prediction.homeTeamId
            } ?: throw IllegalArgumentException("Cannot find home team with ID = ${prediction.homeTeamId}")
            val awayTeam = mutableWorldRugbyRankings.find { worldRugbyRanking ->
                worldRugbyRanking.teamId == prediction.awayTeamId
            } ?: throw IllegalArgumentException("Cannot find away team with ID = ${prediction.awayTeamId}")
            val points = pointsForPrediction(homeTeam, awayTeam, prediction)
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

    @VisibleForTesting
    fun pointsForPrediction(
        homeTeam: WorldRugbyRanking,
        awayTeam: WorldRugbyRanking,
        prediction: Prediction
    ): Float {
        // The effective ranking of the home team is an additional 3 points
        val homeTeamPoints = if (!prediction.noHomeAdvantage) homeTeam.points + 3f else homeTeam.points
        // Determine the ranking points difference and clamp to 10 points
        val pointsDifference = min(10f, max(-10f, homeTeamPoints - awayTeam.points))
        // A draw gives the home team one tenth of the ranking points difference
        val drawDifference = pointsDifference / 10f
        // Big/small wins/losses and RWC matches multiply rankings changes
        var multiplier = 1f
        // The points multiplier is 1.5 if either team wins by more than 15 match points
        if (abs(prediction.homeTeamScore - prediction.awayTeamScore) > 15) multiplier *= 1.5f
        // If the match takes place during a Rugby World Cup, the multiplier is doubled
        if (prediction.rugbyWorldCup) multiplier *= 2f
        // Calculate the final (zero-sum) result
        // Take into account that if the home side wins, they gain 1 extra point; if they lose, they gain 1 less point
        return (when {
            prediction.homeTeamScore > prediction.awayTeamScore -> 1f
            prediction.awayTeamScore > prediction.homeTeamScore -> -1f
            else -> 0f
        } - drawDifference) * multiplier
    }
}
