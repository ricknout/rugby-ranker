package com.ricknout.rugbyranker.db

import androidx.paging.DataSource
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

    @Query("SELECT * FROM world_rugby_matches WHERE eventSport = :sport AND status = :matchStatus ORDER BY timeMillis ASC")
    fun loadAsc(sport: Sport, matchStatus: MatchStatus): DataSource.Factory<Int, WorldRugbyMatch>

    @Query("SELECT * FROM world_rugby_matches WHERE eventSport = :sport AND status = :matchStatus ORDER BY timeMillis DESC")
    fun loadDesc(sport: Sport, matchStatus: MatchStatus): DataSource.Factory<Int, WorldRugbyMatch>
}
