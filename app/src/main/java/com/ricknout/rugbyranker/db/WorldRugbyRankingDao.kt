package com.ricknout.rugbyranker.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.vo.WorldRugbyRanking

@Dao
interface WorldRugbyRankingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(worldRugbyRankings: List<WorldRugbyRanking>)

    @Query("SELECT * FROM world_rugby_rankings WHERE sport = :sport ORDER BY position ASC")
    fun load(sport: Sport): LiveData<List<WorldRugbyRanking>>
}
