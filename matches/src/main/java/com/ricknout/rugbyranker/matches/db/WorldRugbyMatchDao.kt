package com.ricknout.rugbyranker.matches.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch

@Dao
interface WorldRugbyMatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(worldRugbyMatches: List<WorldRugbyMatch>)

    // BUG: Using arg0, arg1, arg2 because of issue with DataSource.Factory
    // https://issuetracker.google.com/issues/119738980
    @Query("SELECT * FROM world_rugby_matches WHERE eventSport = :arg0 AND status = :arg1 AND timeMillis > :arg2 ORDER BY timeMillis ASC")
    fun loadAsc(sport: Sport, matchStatus: MatchStatus, millis: Long): DataSource.Factory<Int, WorldRugbyMatch>

    // BUG: Using arg0, arg1, arg2 because of issue with DataSource.Factory
    // https://issuetracker.google.com/issues/119738980
    @Query("SELECT * FROM world_rugby_matches WHERE eventSport = :arg0 AND status = :arg1 AND timeMillis < :arg2 ORDER BY timeMillis DESC")
    fun loadDesc(sport: Sport, matchStatus: MatchStatus, millis: Long): DataSource.Factory<Int, WorldRugbyMatch>

    @Query("SELECT EXISTS(SELECT * FROM world_rugby_matches WHERE timeMillis > :startMillis AND timeMillis < :endMillis)")
    suspend fun hasBetween(startMillis: Long, endMillis: Long): Boolean
}
