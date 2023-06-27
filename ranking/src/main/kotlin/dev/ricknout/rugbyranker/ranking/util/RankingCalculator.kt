package dev.ricknout.rugbyranker.ranking.util

import androidx.annotation.VisibleForTesting
import dev.ricknout.rugbyranker.core.model.Ranking
import dev.ricknout.rugbyranker.prediction.model.Prediction
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object RankingCalculator {

    fun allocatePointsForPredictions(
        rankings: List<Ranking>,
        predictions: List<Prediction>,
    ): List<Ranking> {
        if (predictions.isEmpty()) return rankings
        val mutableRankings = rankings.asSequence().map { ranking ->
            ranking.resetPreviousPoints() // Reset previous points initially
        }.toMutableList()
        predictions.forEach { prediction ->
            val homeRanking = mutableRankings.find { ranking ->
                ranking.teamId == prediction.homeTeam.id
            } ?: throw IllegalArgumentException("Cannot find home team with ID = ${prediction.homeTeam.id}")
            val awayRanking = mutableRankings.find { ranking ->
                ranking.teamId == prediction.awayTeam.id
            } ?: throw IllegalArgumentException("Cannot find away team with ID = ${prediction.awayTeam.id}")
            val points = pointsForPrediction(homeRanking, awayRanking, prediction)
            mutableRankings[mutableRankings.indexOf(homeRanking)] = homeRanking.addPoints(points)
            mutableRankings[mutableRankings.indexOf(awayRanking)] = awayRanking.addPoints(-points)
        }
        return mutableRankings.asSequence().sortedByDescending { ranking ->
            ranking.points
        }.mapIndexed { index, ranking ->
            val position = index.inc()
            ranking.updatePosition(position)
        }.toList()
    }

    @VisibleForTesting
    fun pointsForPrediction(
        homeRanking: Ranking,
        awayRanking: Ranking,
        prediction: Prediction,
    ): Float {
        // The effective ranking of the home team is an additional 3 points
        val homeTeamPoints =
            if (!prediction.noHomeAdvantage) homeRanking.points + 3f else homeRanking.points
        // Determine the ranking points difference and clamp to 10 points
        val pointsDifference = min(10f, max(-10f, homeTeamPoints - awayRanking.points))
        // A draw gives the home team one tenth of the ranking points difference
        val drawDifference = pointsDifference / 10f
        // Big/small wins/losses and RWC matches multiply rankings changes
        var multiplier = 1f
        // The points multiplier is 1.5 if either team wins by more than 15 match points
        if (abs(prediction.homeScore - prediction.awayScore) > 15) multiplier *= 1.5f
        // If the match takes place during a Rugby World Cup, the multiplier is doubled
        if (prediction.rugbyWorldCup) multiplier *= 2f
        // Calculate the final (zero-sum) result
        // Take into account that if the home side wins, they gain 1 extra point; if they lose, they gain 1 less point
        return (
            when {
                prediction.homeScore > prediction.awayScore -> 1f
                prediction.awayScore > prediction.homeScore -> -1f
                else -> 0f
            } - drawDifference
            ) * multiplier
    }
}
