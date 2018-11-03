package com.ricknout.rugbyranker.vo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "world_rugby_rankings")
data class WorldRugbyRanking(
        @PrimaryKey
        val teamId: Long,
        val teamName: String,
        val teamAbbreviation: String,
        val position: Int,
        val previousPosition: Int,
        val points: Float,
        val previousPoints: Float,
        val matches: Int,
        val sport: Sport
) {

    fun resetPreviousPoints() = copy(previousPoints = this.points)

    fun addPoints(points: Float) = copy(points = this.points + points)

    fun updatePosition(position: Int) = copy(previousPosition = this.position, position = position)

    fun pointsDifference() = points - previousPoints
}
