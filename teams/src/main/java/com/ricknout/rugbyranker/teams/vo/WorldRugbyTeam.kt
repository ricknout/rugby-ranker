package com.ricknout.rugbyranker.teams.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ricknout.rugbyranker.core.vo.Sport

@Entity(tableName = "world_rugby_teams")
data class WorldRugbyTeam(
    @PrimaryKey
    val id: Long,
    val name: String,
    val abbreviation: String,
    val sport: Sport
)
