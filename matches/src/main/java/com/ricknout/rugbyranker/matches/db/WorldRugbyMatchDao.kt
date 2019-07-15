package com.ricknout.rugbyranker.matches.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch

@Dao
interface WorldRugbyMatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(worldRugbyMatches: List<WorldRugbyMatch>)

    @Query("SELECT * FROM world_rugby_matches WHERE eventSport = :sport AND status = :matchStatus AND timeMillis > :millis ORDER BY timeMillis ASC")
    fun loadAsc(sport: Sport, matchStatus: MatchStatus, millis: Long): DataSource.Factory<Int, WorldRugbyMatch>

    @Query("SELECT * FROM world_rugby_matches WHERE eventSport = :sport AND status = :matchStatus AND timeMillis < :millis ORDER BY timeMillis DESC")
    fun loadDesc(sport: Sport, matchStatus: MatchStatus, millis: Long): DataSource.Factory<Int, WorldRugbyMatch>

    @Query("SELECT EXISTS(SELECT * FROM world_rugby_matches WHERE timeMillis > :startMillis AND timeMillis < :endMillis)")
    suspend fun hasBetween(startMillis: Long, endMillis: Long): Boolean
}
