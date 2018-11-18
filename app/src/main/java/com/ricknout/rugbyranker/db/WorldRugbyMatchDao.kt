package com.ricknout.rugbyranker.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ricknout.rugbyranker.vo.MatchStatus
import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.vo.WorldRugbyMatch

@Dao
interface WorldRugbyMatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(worldRugbyMatches: List<WorldRugbyMatch>)

    @Query("SELECT * FROM world_rugby_matches WHERE eventSport = :sport AND status = :matchStatus AND timeMillis > :millis AND EXISTS(SELECT * FROM world_rugby_rankings WHERE teamId = firstTeamId) AND EXISTS(SELECT * FROM world_rugby_rankings WHERE teamId = secondTeamId) ORDER BY timeMillis ASC")
    fun loadAsc(sport: Sport, matchStatus: MatchStatus, millis: Long): DataSource.Factory<Int, WorldRugbyMatch>

    @Query("SELECT * FROM world_rugby_matches WHERE eventSport = :sport AND status = :matchStatus AND timeMillis < :millis AND EXISTS(SELECT * FROM world_rugby_rankings WHERE teamId = firstTeamId) AND EXISTS(SELECT * FROM world_rugby_rankings WHERE teamId = secondTeamId) ORDER BY timeMillis DESC")
    fun loadDesc(sport: Sport, matchStatus: MatchStatus, millis: Long): DataSource.Factory<Int, WorldRugbyMatch>
}
