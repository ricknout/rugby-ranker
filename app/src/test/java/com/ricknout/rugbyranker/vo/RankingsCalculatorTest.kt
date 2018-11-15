package com.ricknout.rugbyranker.vo

import org.junit.Test
import org.junit.Assert.assertEquals

class RankingsCalculatorTest {

    @Test
    fun allocatePointsForMatchPrediction() {
        val team1 = WorldRugbyRanking(
                teamId = 1,
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 95f,
                matches = 10,
                sport = Sport.MENS
        )
        val team2 = WorldRugbyRanking(
                teamId = 2,
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 95f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val team3 = WorldRugbyRanking(
                teamId = 3,
                teamName = "Team 3",
                teamAbbreviation = "T3",
                position = 3,
                previousPosition = 4,
                points = 90f,
                previousPoints = 85f,
                matches = 10,
                sport = Sport.MENS
        )
        val team4 = WorldRugbyRanking(
                teamId = 4,
                teamName = "Team 4",
                teamAbbreviation = "T4",
                position = 4,
                previousPosition = 5,
                points = 85f,
                previousPoints = 80f,
                matches = 10,
                sport = Sport.MENS
        )
        val matchPrediction1 = MatchPrediction(
                id = MatchPrediction.generateId(),
                homeTeamId = team1.teamId,
                homeTeamName = team1.teamName,
                homeTeamAbbreviation = team1.teamAbbreviation,
                homeTeamScore = 10,
                awayTeamId = team2.teamId,
                awayTeamName = team2.teamName,
                awayTeamAbbreviation = team2.teamAbbreviation,
                awayTeamScore = 30,
                noHomeAdvantage = true,
                rugbyWorldCup = false
        )
        val matchPrediction2 = MatchPrediction(
                id = MatchPrediction.generateId(),
                homeTeamId = team3.teamId,
                homeTeamName = team3.teamName,
                homeTeamAbbreviation = team3.teamAbbreviation,
                homeTeamScore = 0,
                awayTeamId = team4.teamId,
                awayTeamName = team4.teamName,
                awayTeamAbbreviation = team4.teamAbbreviation,
                awayTeamScore = 5,
                noHomeAdvantage = false,
                rugbyWorldCup = true
        )
        val teams = listOf(team1, team2, team3, team4)
        val matchPredictions = listOf(matchPrediction1, matchPrediction2)
        val expectedTeam1 = team1.copy(points = 97.75f, previousPoints = 100f, position = 1, previousPosition = 1)
        val expectedTeam2 = team2.copy(points = 97.25f, previousPoints = 95f, position = 2, previousPosition = 2)
        val expectedTeam3 = team3.copy(points = 86.4f, previousPoints = 90f, position = 4, previousPosition = 3)
        val expectedTeam4 = team4.copy(points = 88.6f, previousPoints = 85f, position = 3, previousPosition = 4)
        val expectedTeams = listOf(expectedTeam1, expectedTeam2, expectedTeam4, expectedTeam3)
        assertEquals(RankingsCalculator.allocatePointsForMatchPredictions(teams, matchPredictions), expectedTeams)
    }

    @Test
    fun pointsForMatchPrediction_TeamsPointsEqual() {
        val team1 = WorldRugbyRanking(
                teamId = 1,
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val team2 = WorldRugbyRanking(
                teamId = 2,
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val baseMatchPrediction = MatchPrediction(
                id = MatchPrediction.generateId(),
                homeTeamId = team1.teamId,
                homeTeamName = team1.teamName,
                homeTeamAbbreviation = team1.teamAbbreviation,
                homeTeamScore = 0,
                awayTeamId = team2.teamId,
                awayTeamName = team2.teamName,
                awayTeamAbbreviation = team2.teamAbbreviation,
                awayTeamScore = 0,
                noHomeAdvantage = false,
                rugbyWorldCup = false
        )
        // Draw
        val drawMatchPrediction = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 50)
        val pointsForDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForDrawMatchPrediction, -0.3f)
        val pointsForReverseDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForReverseDrawMatchPrediction, -0.3f)
        // Score <= 15
        val scoreLess15Result = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 40)
        val pointsForScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 0.7f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 50)
        val pointsForReverseScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -1.3f)
        // Score > 15
        val scoreMore15Result = baseMatchPrediction.copy(homeTeamScore = 60, awayTeamScore = 40)
        val pointsForScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 1.05f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 60)
        val pointsForReverseScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -1.9499999f)
    }

    @Test
    fun pointsForMatchPrediction_TeamsPointsEqual_NHA() {
        val team1 = WorldRugbyRanking(
                teamId = 1,
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val team2 = WorldRugbyRanking(
                teamId = 2,
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val baseMatchPrediction = MatchPrediction(
                id = MatchPrediction.generateId(),
                homeTeamId = team1.teamId,
                homeTeamName = team1.teamName,
                homeTeamAbbreviation = team1.teamAbbreviation,
                homeTeamScore = 0,
                awayTeamId = team2.teamId,
                awayTeamName = team2.teamName,
                awayTeamAbbreviation = team2.teamAbbreviation,
                awayTeamScore = 0,
                noHomeAdvantage = true,
                rugbyWorldCup = false
        )
        // Draw
        val drawMatchPrediction = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 50)
        val pointsForDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForDrawMatchPrediction, 0f)
        val pointsForReverseDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForReverseDrawMatchPrediction, 0f)
        // Score <= 15
        val scoreLess15Result = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 40)
        val pointsForScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 1f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 50)
        val pointsForReverseScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -1f)
        // Score > 15
        val scoreMore15Result = baseMatchPrediction.copy(homeTeamScore = 60, awayTeamScore = 40)
        val pointsForScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 1.5f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 60)
        val pointsForReverseScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -1.5f)
    }

    @Test
    fun pointsForMatchPrediction_TeamsPointsEqual_RWC() {
        val team1 = WorldRugbyRanking(
                teamId = 1,
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val team2 = WorldRugbyRanking(
                teamId = 2,
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val baseMatchPrediction = MatchPrediction(
                id = MatchPrediction.generateId(),
                homeTeamId = team1.teamId,
                homeTeamName = team1.teamName,
                homeTeamAbbreviation = team1.teamAbbreviation,
                homeTeamScore = 0,
                awayTeamId = team2.teamId,
                awayTeamName = team2.teamName,
                awayTeamAbbreviation = team2.teamAbbreviation,
                awayTeamScore = 0,
                noHomeAdvantage = false,
                rugbyWorldCup = true
        )
        // Draw
        val drawMatchPrediction = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 50)
        val pointsForDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForDrawMatchPrediction, -0.6f)
        val pointsForReverseDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForReverseDrawMatchPrediction, -0.6f)
        // Score <= 15
        val scoreLess15Result = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 40)
        val pointsForScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 1.4f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 50)
        val pointsForReverseScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -2.6f)
        // Score > 15
        val scoreMore15Result = baseMatchPrediction.copy(homeTeamScore = 60, awayTeamScore = 40)
        val pointsForScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 2.1f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 60)
        val pointsForReverseScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -3.8999999f)
    }

    @Test
    fun pointsForMatchPrediction_TeamsPointsNotEqual() {
        val team1 = WorldRugbyRanking(
                teamId = 1,
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val team2 = WorldRugbyRanking(
                teamId = 2,
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 95f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val baseMatchPrediction = MatchPrediction(
                id = MatchPrediction.generateId(),
                homeTeamId = team1.teamId,
                homeTeamName = team1.teamName,
                homeTeamAbbreviation = team1.teamAbbreviation,
                homeTeamScore = 0,
                awayTeamId = team2.teamId,
                awayTeamName = team2.teamName,
                awayTeamAbbreviation = team2.teamAbbreviation,
                awayTeamScore = 0,
                noHomeAdvantage = false,
                rugbyWorldCup = false
        )
        // Draw
        val drawMatchPrediction = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 50)
        val pointsForDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForDrawMatchPrediction, -0.8f)
        val pointsForReverseDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForReverseDrawMatchPrediction, -0.8f)
        // Score <= 15
        val scoreLess15Result = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 40)
        val pointsForScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 0.19999999f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 50)
        val pointsForReverseScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -1.8f)
        // Score > 15
        val scoreMore15Result = baseMatchPrediction.copy(homeTeamScore = 60, awayTeamScore = 40)
        val pointsForScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 0.29999998f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 60)
        val pointsForReverseScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -2.6999998f)
    }

    @Test
    fun pointsForMatchPrediction_TeamsPointsNotEqual_NHA() {
        val team1 = WorldRugbyRanking(
                teamId = 1,
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val team2 = WorldRugbyRanking(
                teamId = 2,
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 95f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val baseMatchPrediction = MatchPrediction(
                id = MatchPrediction.generateId(),
                homeTeamId = team1.teamId,
                homeTeamName = team1.teamName,
                homeTeamAbbreviation = team1.teamAbbreviation,
                homeTeamScore = 0,
                awayTeamId = team2.teamId,
                awayTeamName = team2.teamName,
                awayTeamAbbreviation = team2.teamAbbreviation,
                awayTeamScore = 0,
                noHomeAdvantage = true,
                rugbyWorldCup = false
        )
        // Draw
        val drawMatchPrediction = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 50)
        val pointsForDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForDrawMatchPrediction, -0.5f)
        val pointsForReverseDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForReverseDrawMatchPrediction, -0.5f)
        // Score <= 15
        val scoreLess15Result = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 40)
        val pointsForScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 0.5f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 50)
        val pointsForReverseScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -1.5f)
        // Score > 15
        val scoreMore15Result = baseMatchPrediction.copy(homeTeamScore = 60, awayTeamScore = 40)
        val pointsForScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 0.75f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 60)
        val pointsForReverseScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -2.25f)
    }

    @Test
    fun pointsForMatchPrediction_TeamsPointsNotEqual_RWC() {
        val team1 = WorldRugbyRanking(
                teamId = 1,
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val team2 = WorldRugbyRanking(
                teamId = 2,
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 95f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val baseMatchPrediction = MatchPrediction(
                id = MatchPrediction.generateId(),
                homeTeamId = team1.teamId,
                homeTeamName = team1.teamName,
                homeTeamAbbreviation = team1.teamAbbreviation,
                homeTeamScore = 0,
                awayTeamId = team2.teamId,
                awayTeamName = team2.teamName,
                awayTeamAbbreviation = team2.teamAbbreviation,
                awayTeamScore = 0,
                noHomeAdvantage = false,
                rugbyWorldCup = true
        )
        // Draw
        val drawMatchPrediction = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 50)
        val pointsForDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForDrawMatchPrediction, -1.6f)
        val pointsForReverseDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForReverseDrawMatchPrediction, -1.6f)
        // Score <= 15
        val scoreLess15Result = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 40)
        val pointsForScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 0.39999998f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 50)
        val pointsForReverseScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -3.6f)
        // Score > 15
        val scoreMore15Result = baseMatchPrediction.copy(homeTeamScore = 60, awayTeamScore = 40)
        val pointsForScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 0.59999996f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 60)
        val pointsForReverseScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -5.3999996f)
    }

    @Test
    fun pointsForMatchPrediction_TeamsPointsMoreThan10() {
        val team1 = WorldRugbyRanking(
                teamId = 1,
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val team2 = WorldRugbyRanking(
                teamId = 2,
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 80f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val baseMatchPrediction = MatchPrediction(
                id = MatchPrediction.generateId(),
                homeTeamId = team1.teamId,
                homeTeamName = team1.teamName,
                homeTeamAbbreviation = team1.teamAbbreviation,
                homeTeamScore = 0,
                awayTeamId = team2.teamId,
                awayTeamName = team2.teamName,
                awayTeamAbbreviation = team2.teamAbbreviation,
                awayTeamScore = 0,
                noHomeAdvantage = false,
                rugbyWorldCup = false
        )
        // Draw
        val drawMatchPrediction = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 50)
        val pointsForDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForDrawMatchPrediction, -1f)
        val pointsForReverseDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForReverseDrawMatchPrediction, -1f)
        // Score <= 15
        val scoreLess15Result = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 40)
        val pointsForScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 0f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 50)
        val pointsForReverseScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -2f)
        // Score > 15
        val scoreMore15Result = baseMatchPrediction.copy(homeTeamScore = 60, awayTeamScore = 40)
        val pointsForScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 0f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 60)
        val pointsForReverseScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -3f)
    }

    @Test
    fun pointsForMatchPrediction_TeamsPointsMoreThan10_NHA() {
        val team1 = WorldRugbyRanking(
                teamId = 1,
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val team2 = WorldRugbyRanking(
                teamId = 2,
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 80f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val baseMatchPrediction = MatchPrediction(
                id = MatchPrediction.generateId(),
                homeTeamId = team1.teamId,
                homeTeamName = team1.teamName,
                homeTeamAbbreviation = team1.teamAbbreviation,
                homeTeamScore = 0,
                awayTeamId = team2.teamId,
                awayTeamName = team2.teamName,
                awayTeamAbbreviation = team2.teamAbbreviation,
                awayTeamScore = 0,
                noHomeAdvantage = true,
                rugbyWorldCup = false
        )
        // Draw
        val drawMatchPrediction = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 50)
        val pointsForDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForDrawMatchPrediction, -1f)
        val pointsForReverseDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForReverseDrawMatchPrediction, -1f)
        // Score <= 15
        val scoreLess15Result = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 40)
        val pointsForScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 0f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 50)
        val pointsForReverseScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -2f)
        // Score > 15
        val scoreMore15Result = baseMatchPrediction.copy(homeTeamScore = 60, awayTeamScore = 40)
        val pointsForScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 0f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 60)
        val pointsForReverseScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -3f)
    }

    @Test
    fun pointsForMatchPrediction_TeamsPointsMoreThan10_RWC() {
        val team1 = WorldRugbyRanking(
                teamId = 1,
                teamName = "Team 1",
                teamAbbreviation = "T1",
                position = 1,
                previousPosition = 2,
                points = 100f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val team2 = WorldRugbyRanking(
                teamId = 2,
                teamName = "Team 2",
                teamAbbreviation = "T2",
                position = 2,
                previousPosition = 3,
                points = 80f,
                previousPoints = 90f,
                matches = 10,
                sport = Sport.MENS
        )
        val baseMatchPrediction = MatchPrediction(
                id = MatchPrediction.generateId(),
                homeTeamId = team1.teamId,
                homeTeamName = team1.teamName,
                homeTeamAbbreviation = team1.teamAbbreviation,
                homeTeamScore = 0,
                awayTeamId = team2.teamId,
                awayTeamName = team2.teamName,
                awayTeamAbbreviation = team2.teamAbbreviation,
                awayTeamScore = 0,
                noHomeAdvantage = false,
                rugbyWorldCup = true
        )
        // Draw
        val drawMatchPrediction = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 50)
        val pointsForDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForDrawMatchPrediction, -2f)
        val pointsForReverseDrawMatchPrediction = RankingsCalculator.pointsForMatchPrediction(team1, team2, drawMatchPrediction)
        assertEquals(pointsForReverseDrawMatchPrediction, -2f)
        // Score <= 15
        val scoreLess15Result = baseMatchPrediction.copy(homeTeamScore = 50, awayTeamScore = 40)
        val pointsForScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreLess15Result)
        assertEquals(pointsForScoreLess15Result, 0f)
        val reverseScoreLess15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 50)
        val pointsForReverseScoreLess15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreLess15Result)
        assertEquals(pointsForReverseScoreLess15Result, -4f)
        // Score > 15
        val scoreMore15Result = baseMatchPrediction.copy(homeTeamScore = 60, awayTeamScore = 40)
        val pointsForScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, scoreMore15Result)
        assertEquals(pointsForScoreMore15Result, 0f)
        val reverseScoreMore15Result = scoreLess15Result.copy(homeTeamScore = 40, awayTeamScore = 60)
        val pointsForReverseScoreMore15Result = RankingsCalculator.pointsForMatchPrediction(team1, team2, reverseScoreMore15Result)
        assertEquals(pointsForReverseScoreMore15Result, -6f)
    }
}
