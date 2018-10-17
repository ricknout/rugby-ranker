package com.ricknout.worldrugbyranker.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "world_rugby_rankings")
data class WorldRugbyRanking(
        @PrimaryKey
        @field:SerializedName("team_id")
        val teamId: Long,
        @field:SerializedName("team_name")
        val teamName: String,
        @field:SerializedName("team_abbreviation")
        val teamAbbreviation: String,
        @field:SerializedName("position")
        val position: Int,
        @field:SerializedName("previous_position")
        val previousPosition: Int,
        @field:SerializedName("points")
        val points: Float,
        @field:SerializedName("previous_points")
        val previousPoints: Float,
        @field:SerializedName("matches")
        val matches: Int,
        @field:SerializedName("rankings_type")
        val rankingsType: RankingsType
) {

        fun allocatePoints(points: Float) = copy(previousPoints = this.points, points = this.points + points)

        fun updatePosition(position: Int) = copy(previousPosition = this.position, position = position)

        fun pointsDifference() = points - previousPoints
}
