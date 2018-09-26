package com.ricknout.worldrugbyranker.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

abstract class WorldRugbyRanking {

        abstract val teamId: Long
        abstract val teamName: String
        abstract val teamAbbreviation: String
        abstract val position: Int
        abstract val previousPosition: Int
        abstract val points: Float
        abstract val previousPoints: Float
        abstract val matches: Int

        abstract fun allocatePoints(points: Float): WorldRugbyRanking

        abstract fun updatePosition(position: Int): WorldRugbyRanking
}

@Entity(tableName = "mens_world_rugby_rankings")
data class MensWorldRugbyRanking(
        @PrimaryKey
        @field:SerializedName("team_id")
        override val teamId: Long,
        @field:SerializedName("team_name")
        override val teamName: String,
        @field:SerializedName("team_abbreviation")
        override val teamAbbreviation: String,
        @field:SerializedName("position")
        override val position: Int,
        @field:SerializedName("previous_position")
        override val previousPosition: Int,
        @field:SerializedName("points")
        override val points: Float,
        @field:SerializedName("previous_points")
        override val previousPoints: Float,
        @field:SerializedName("matches")
        override val matches: Int
) : WorldRugbyRanking() {

        override fun allocatePoints(points: Float) = copy(previousPoints = this.points, points = this.points + points)

        override fun updatePosition(position: Int) = copy(previousPosition = this.position, position = position)
}

@Entity(tableName = "womens_world_rugby_rankings")
data class WomensWorldRugbyRanking(
        @PrimaryKey
        @field:SerializedName("team_id")
        override val teamId: Long,
        @field:SerializedName("team_name")
        override val teamName: String,
        @field:SerializedName("team_abbreviation")
        override val teamAbbreviation: String,
        @field:SerializedName("position")
        override val position: Int,
        @field:SerializedName("previous_position")
        override val previousPosition: Int,
        @field:SerializedName("points")
        override val points: Float,
        @field:SerializedName("previous_points")
        override val previousPoints: Float,
        @field:SerializedName("matches")
        override val matches: Int
) : WorldRugbyRanking() {

        override fun allocatePoints(points: Float) = copy(previousPoints = this.points, points = this.points + points)

        override fun updatePosition(position: Int) = copy(previousPosition = this.position, position = position)
}