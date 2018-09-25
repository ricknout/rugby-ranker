package com.ricknout.worldrugbyranker.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ricknout.worldrugbyranker.vo.MensWorldRugbyRanking
import com.ricknout.worldrugbyranker.vo.WomensWorldRugbyRanking

@Dao
interface WorldRugbyRankingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMens(worldRugbyRankings: List<MensWorldRugbyRanking>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWomens(worldRugbyRankings: List<WomensWorldRugbyRanking>)

    @Query("SELECT * FROM mens_world_rugby_rankings ORDER BY position")
    fun loadMens(): LiveData<List<MensWorldRugbyRanking>>

    @Query("SELECT * FROM womens_world_rugby_rankings ORDER BY position")
    fun loadWomens(): LiveData<List<WomensWorldRugbyRanking>>
}
