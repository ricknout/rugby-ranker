package com.ricknout.rugbyranker.rankings.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.rankings.vo.WorldRugbyRanking

@Dao
interface WorldRugbyRankingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(worldRugbyRankings: List<WorldRugbyRanking>)

    @Query("SELECT * FROM world_rugby_rankings WHERE sport = :sport ORDER BY position ASC")
    fun load(sport: Sport): LiveData<List<WorldRugbyRanking>>

    @Query("SELECT teamId FROM world_rugby_rankings WHERE sport = :sport")
    fun loadTeamIds(sport: Sport): LiveData<List<Long>>
}
