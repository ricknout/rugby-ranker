package com.ricknout.rugbyranker.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ricknout.rugbyranker.vo.MatchStatus
import com.ricknout.rugbyranker.vo.Sport
import com.ricknout.rugbyranker.vo.WorldRugbyMatch

@Dao
interface WorldRugbyMatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(worldRugbyMatches: List<WorldRugbyMatch>)

    @Query("SELECT * FROM world_rugby_matches WHERE eventSport = :sport AND status = :matchStatus AND timeMillis < :millis ORDER BY timeMillis DESC")
    fun loadBefore(sport: Sport, matchStatus: MatchStatus, millis: Long): List<WorldRugbyMatch>

    @Query("SELECT * FROM world_rugby_matches WHERE eventSport = :sport AND status = :matchStatus AND timeMillis > :millis ORDER BY timeMillis ASC")
    fun loadAfter(sport: Sport, matchStatus: MatchStatus, millis: Long): List<WorldRugbyMatch>
}
