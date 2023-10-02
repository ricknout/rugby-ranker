package dev.ricknout.rugbyranker.ranking.util

import dev.ricknout.rugbyranker.core.model.Ranking
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.prediction.model.Prediction
import dev.ricknout.rugbyranker.prediction.model.Team
import org.junit.Assert.assertEquals
import org.junit.Test

class RankingCalculatorTest {
    @Test
    fun allocatePointsForPrediction() {
        val team1 =
            Ranking(
                teamId = "1",
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 95f,
                matches = 10,
                sport = Sport.MENS,
            )
        val team2 =
            Ranking(
                teamId = "2",
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 95f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val team3 =
            Ranking(
                teamId = "3",
                teamName = "Team 3",
                teamAbbreviation = "T3",
                position = 3,
                previousPosition = 4,
                points = 90f,
                previousPoints = 85f,
                matches = 10,
                sport = Sport.MENS,
            )
        val team4 =
            Ranking(
                teamId = "4",
                teamName = "Team 4",
                teamAbbreviation = "T4",
                position = 4,
                previousPosition = 5,
                points = 85f,
                previousPoints = 80f,
                matches = 10,
                sport = Sport.MENS,
            )
        val prediction1 =
            Prediction(
                id = Prediction.generateId(),
                homeTeam =
                    Team(
                        id = team1.teamId,
                        name = team1.teamName,
                        abbreviation = team1.teamAbbreviation,
                    ),
                homeScore = 10,
                awayTeam =
                    Team(
                        id = team2.teamId,
                        name = team2.teamName,
                        abbreviation = team2.teamAbbreviation,
                    ),
                awayScore = 30,
                noHomeAdvantage = true,
                rugbyWorldCup = false,
            )
        val prediction2 =
            Prediction(
                id = Prediction.generateId(),
                homeTeam =
                    Team(
                        id = team3.teamId,
                        name = team3.teamName,
                        abbreviation = team3.teamAbbreviation,
                    ),
                homeScore = 0,
                awayTeam =
                    Team(
                        id = team4.teamId,
                        name = team4.teamName,
                        abbreviation = team4.teamAbbreviation,
                    ),
                awayScore = 5,
                noHomeAdvantage = false,
                rugbyWorldCup = true,
            )
        val teams = listOf(team1, team2, team3, team4)
        val predictions = listOf(prediction1, prediction2)
        val expectedTeam1 = team1.copy(points = 97.75f, previousPoints = 100f, position = 1, previousPosition = 1)
        val expectedTeam2 = team2.copy(points = 97.25f, previousPoints = 95f, position = 2, previousPosition = 2)
        val expectedTeam3 = team3.copy(points = 86.4f, previousPoints = 90f, position = 4, previousPosition = 3)
        val expectedTeam4 = team4.copy(points = 88.6f, previousPoints = 85f, position = 3, previousPosition = 4)
        val expectedTeams = listOf(expectedTeam1, expectedTeam2, expectedTeam4, expectedTeam3)
        assertEquals(RankingCalculator.allocatePointsForPredictions(teams, predictions), expectedTeams)
    }

    @Test
    fun pointsForPrediction_TeamsPointsEqual() {
        val team1 =
            Ranking(
                teamId = "1",
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val team2 =
            Ranking(
                teamId = "2",
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val basePrediction =
            Prediction(
                id = Prediction.generateId(),
                homeTeam =
                    Team(
                        id = team1.teamId,
                        name = team1.teamName,
                        abbreviation = team1.teamAbbreviation,
                    ),
                homeScore = 0,
                awayTeam =
                    Team(
                        id = team2.teamId,
                        name = team2.teamName,
                        abbreviation = team2.teamAbbreviation,
                    ),
                awayScore = 0,
                noHomeAdvantage = false,
                rugbyWorldCup = false,
            )
        // Draw
        val drawPrediction = basePrediction.copy(homeScore = 50, awayScore = 50)
        val pointsForDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForDrawPrediction, -0.3f)
        val pointsForReverseDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForReverseDrawPrediction, -0.3f)
        // Score <= 15
        val scoreLess15Result = basePrediction.copy(homeScore = 50, awayScore = 40)
        val pointsForScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 0.7f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 50)
        val pointsForReverseScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -1.3f)
        // Score > 15
        val scoreMore15Result = basePrediction.copy(homeScore = 60, awayScore = 40)
        val pointsForScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 1.05f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 60)
        val pointsForReverseScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -1.9499999f)
    }

    @Test
    fun pointsForPrediction_TeamsPointsEqual_NHA() {
        val team1 =
            Ranking(
                teamId = "1",
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val team2 =
            Ranking(
                teamId = "2",
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val basePrediction =
            Prediction(
                id = Prediction.generateId(),
                homeTeam =
                    Team(
                        id = team1.teamId,
                        name = team1.teamName,
                        abbreviation = team1.teamAbbreviation,
                    ),
                homeScore = 0,
                awayTeam =
                    Team(
                        id = team2.teamId,
                        name = team2.teamName,
                        abbreviation = team2.teamAbbreviation,
                    ),
                awayScore = 0,
                noHomeAdvantage = true,
                rugbyWorldCup = false,
            )
        // Draw
        val drawPrediction = basePrediction.copy(homeScore = 50, awayScore = 50)
        val pointsForDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForDrawPrediction, 0f)
        val pointsForReverseDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForReverseDrawPrediction, 0f)
        // Score <= 15
        val scoreLess15Result = basePrediction.copy(homeScore = 50, awayScore = 40)
        val pointsForScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 1f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 50)
        val pointsForReverseScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -1f)
        // Score > 15
        val scoreMore15Result = basePrediction.copy(homeScore = 60, awayScore = 40)
        val pointsForScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 1.5f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 60)
        val pointsForReverseScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -1.5f)
    }

    @Test
    fun pointsForPrediction_TeamsPointsEqual_RWC() {
        val team1 =
            Ranking(
                teamId = "1",
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val team2 =
            Ranking(
                teamId = "2",
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val basePrediction =
            Prediction(
                id = Prediction.generateId(),
                homeTeam =
                    Team(
                        id = team1.teamId,
                        name = team1.teamName,
                        abbreviation = team1.teamAbbreviation,
                    ),
                homeScore = 0,
                awayTeam =
                    Team(
                        id = team2.teamId,
                        name = team2.teamName,
                        abbreviation = team2.teamAbbreviation,
                    ),
                awayScore = 0,
                noHomeAdvantage = false,
                rugbyWorldCup = true,
            )
        // Draw
        val drawPrediction = basePrediction.copy(homeScore = 50, awayScore = 50)
        val pointsForDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForDrawPrediction, -0.6f)
        val pointsForReverseDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForReverseDrawPrediction, -0.6f)
        // Score <= 15
        val scoreLess15Result = basePrediction.copy(homeScore = 50, awayScore = 40)
        val pointsForScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 1.4f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 50)
        val pointsForReverseScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -2.6f)
        // Score > 15
        val scoreMore15Result = basePrediction.copy(homeScore = 60, awayScore = 40)
        val pointsForScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 2.1f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 60)
        val pointsForReverseScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -3.8999999f)
    }

    @Test
    fun pointsForPrediction_TeamsPointsNotEqual() {
        val team1 =
            Ranking(
                teamId = "1",
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val team2 =
            Ranking(
                teamId = "2",
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 95f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val basePrediction =
            Prediction(
                id = Prediction.generateId(),
                homeTeam =
                    Team(
                        id = team1.teamId,
                        name = team1.teamName,
                        abbreviation = team1.teamAbbreviation,
                    ),
                homeScore = 0,
                awayTeam =
                    Team(
                        id = team2.teamId,
                        name = team2.teamName,
                        abbreviation = team2.teamAbbreviation,
                    ),
                awayScore = 0,
                noHomeAdvantage = false,
                rugbyWorldCup = false,
            )
        // Draw
        val drawPrediction = basePrediction.copy(homeScore = 50, awayScore = 50)
        val pointsForDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForDrawPrediction, -0.8f)
        val pointsForReverseDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForReverseDrawPrediction, -0.8f)
        // Score <= 15
        val scoreLess15Result = basePrediction.copy(homeScore = 50, awayScore = 40)
        val pointsForScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 0.19999999f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 50)
        val pointsForReverseScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -1.8f)
        // Score > 15
        val scoreMore15Result = basePrediction.copy(homeScore = 60, awayScore = 40)
        val pointsForScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 0.29999998f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 60)
        val pointsForReverseScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -2.6999998f)
    }

    @Test
    fun pointsForPrediction_TeamsPointsNotEqual_NHA() {
        val team1 =
            Ranking(
                teamId = "1",
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val team2 =
            Ranking(
                teamId = "2",
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 95f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val basePrediction =
            Prediction(
                id = Prediction.generateId(),
                homeTeam =
                    Team(
                        id = team1.teamId,
                        name = team1.teamName,
                        abbreviation = team1.teamAbbreviation,
                    ),
                homeScore = 0,
                awayTeam =
                    Team(
                        id = team2.teamId,
                        name = team2.teamName,
                        abbreviation = team2.teamAbbreviation,
                    ),
                awayScore = 0,
                noHomeAdvantage = true,
                rugbyWorldCup = false,
            )
        // Draw
        val drawPrediction = basePrediction.copy(homeScore = 50, awayScore = 50)
        val pointsForDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForDrawPrediction, -0.5f)
        val pointsForReverseDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForReverseDrawPrediction, -0.5f)
        // Score <= 15
        val scoreLess15Result = basePrediction.copy(homeScore = 50, awayScore = 40)
        val pointsForScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 0.5f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 50)
        val pointsForReverseScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -1.5f)
        // Score > 15
        val scoreMore15Result = basePrediction.copy(homeScore = 60, awayScore = 40)
        val pointsForScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 0.75f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 60)
        val pointsForReverseScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -2.25f)
    }

    @Test
    fun pointsForPrediction_TeamsPointsNotEqual_RWC() {
        val team1 =
            Ranking(
                teamId = "1",
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val team2 =
            Ranking(
                teamId = "2",
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 95f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val basePrediction =
            Prediction(
                id = Prediction.generateId(),
                homeTeam =
                    Team(
                        id = team1.teamId,
                        name = team1.teamName,
                        abbreviation = team1.teamAbbreviation,
                    ),
                homeScore = 0,
                awayTeam =
                    Team(
                        id = team2.teamId,
                        name = team2.teamName,
                        abbreviation = team2.teamAbbreviation,
                    ),
                awayScore = 0,
                noHomeAdvantage = false,
                rugbyWorldCup = true,
            )
        // Draw
        val drawPrediction = basePrediction.copy(homeScore = 50, awayScore = 50)
        val pointsForDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForDrawPrediction, -1.6f)
        val pointsForReverseDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForReverseDrawPrediction, -1.6f)
        // Score <= 15
        val scoreLess15Result = basePrediction.copy(homeScore = 50, awayScore = 40)
        val pointsForScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 0.39999998f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 50)
        val pointsForReverseScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -3.6f)
        // Score > 15
        val scoreMore15Result = basePrediction.copy(homeScore = 60, awayScore = 40)
        val pointsForScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 0.59999996f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 60)
        val pointsForReverseScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -5.3999996f)
    }

    @Test
    fun pointsForPrediction_TeamsPointsMoreThan10() {
        val team1 =
            Ranking(
                teamId = "1",
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val team2 =
            Ranking(
                teamId = "2",
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 80f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val basePrediction =
            Prediction(
                id = Prediction.generateId(),
                homeTeam =
                    Team(
                        id = team1.teamId,
                        name = team1.teamName,
                        abbreviation = team1.teamAbbreviation,
                    ),
                homeScore = 0,
                awayTeam =
                    Team(
                        id = team2.teamId,
                        name = team2.teamName,
                        abbreviation = team2.teamAbbreviation,
                    ),
                awayScore = 0,
                noHomeAdvantage = false,
                rugbyWorldCup = false,
            )
        // Draw
        val drawPrediction = basePrediction.copy(homeScore = 50, awayScore = 50)
        val pointsForDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForDrawPrediction, -1f)
        val pointsForReverseDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForReverseDrawPrediction, -1f)
        // Score <= 15
        val scoreLess15Result = basePrediction.copy(homeScore = 50, awayScore = 40)
        val pointsForScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 0f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 50)
        val pointsForReverseScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -2f)
        // Score > 15
        val scoreMore15Result = basePrediction.copy(homeScore = 60, awayScore = 40)
        val pointsForScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 0f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 60)
        val pointsForReverseScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -3f)
    }

    @Test
    fun pointsForPrediction_TeamsPointsMoreThan10_NHA() {
        val team1 =
            Ranking(
                teamId = "1",
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val team2 =
            Ranking(
                teamId = "2",
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 80f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val basePrediction =
            Prediction(
                id = Prediction.generateId(),
                homeTeam =
                    Team(
                        id = team1.teamId,
                        name = team1.teamName,
                        abbreviation = team1.teamAbbreviation,
                    ),
                homeScore = 0,
                awayTeam =
                    Team(
                        id = team2.teamId,
                        name = team2.teamName,
                        abbreviation = team2.teamAbbreviation,
                    ),
                awayScore = 0,
                noHomeAdvantage = true,
                rugbyWorldCup = false,
            )
        // Draw
        val drawPrediction = basePrediction.copy(homeScore = 50, awayScore = 50)
        val pointsForDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForDrawPrediction, -1f)
        val pointsForReverseDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForReverseDrawPrediction, -1f)
        // Score <= 15
        val scoreLess15Result = basePrediction.copy(homeScore = 50, awayScore = 40)
        val pointsForScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 0f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 50)
        val pointsForReverseScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -2f)
        // Score > 15
        val scoreMore15Result = basePrediction.copy(homeScore = 60, awayScore = 40)
        val pointsForScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 0f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 60)
        val pointsForReverseScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -3f)
    }

    @Test
    fun pointsForPrediction_TeamsPointsMoreThan10_RWC() {
        val team1 =
            Ranking(
                teamId = "1",
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val team2 =
            Ranking(
                teamId = "2",
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 80f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS,
            )
        val basePrediction =
            Prediction(
                id = Prediction.generateId(),
                homeTeam =
                    Team(
                        id = team1.teamId,
                        name = team1.teamName,
                        abbreviation = team1.teamAbbreviation,
                    ),
                homeScore = 0,
                awayTeam =
                    Team(
                        id = team2.teamId,
                        name = team2.teamName,
                        abbreviation = team2.teamAbbreviation,
                    ),
                awayScore = 0,
                noHomeAdvantage = false,
                rugbyWorldCup = true,
            )
        // Draw
        val drawPrediction = basePrediction.copy(homeScore = 50, awayScore = 50)
        val pointsForDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForDrawPrediction, -2f)
        val pointsForReverseDrawPrediction = RankingCalculator.pointsForPrediction(team1, team2, drawPrediction)
        assertEquals(pointsForReverseDrawPrediction, -2f)
        // Score <= 15
        val scoreLess15Result = basePrediction.copy(homeScore = 50, awayScore = 40)
        val pointsForScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 0f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 50)
        val pointsForReverseScoreLess15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -4f)
        // Score > 15
        val scoreMore15Result = basePrediction.copy(homeScore = 60, awayScore = 40)
        val pointsForScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 0f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeScore = 40, awayScore = 60)
        val pointsForReverseScoreMore15Result = RankingCalculator.pointsForPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -6f)
    }
}
