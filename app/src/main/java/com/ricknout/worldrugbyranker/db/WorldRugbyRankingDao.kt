package com.ricknout.worldrugbyranker.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ricknout.worldrugbyranker.vo.RankingsType
import com.ricknout.worldrugbyranker.vo.WorldRugbyRanking

@Dao
interface WorldRugbyRankingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(worldRugbyRankings: List<WorldRugbyRanking>)

    @Query("SELECT * FROM world_rugby_rankings WHERE rankingsType = :rankingsType ORDER BY position")
    fun load(rankingsType: RankingsType): LiveData<List<WorldRugbyRanking>>
}
