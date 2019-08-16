package com.ricknout.rugbyranker.teams.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.teams.vo.WorldRugbyTeam

@Dao
interface WorldRugbyTeamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(worldRugbyTeams: List<WorldRugbyTeam>)

    @Query("SELECT * FROM world_rugby_teams WHERE sport = :sport ORDER BY name ASC")
    fun load(sport: Sport): LiveData<List<WorldRugbyTeam>>
}
