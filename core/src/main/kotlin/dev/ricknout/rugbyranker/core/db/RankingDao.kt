package dev.ricknout.rugbyranker.core.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.ricknout.rugbyranker.core.model.Ranking
import dev.ricknout.rugbyranker.core.model.Sport
import kotlinx.coroutines.flow.Flow

@Dao
interface RankingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rankings: List<Ranking>)

    @Query("SELECT * FROM rankings WHERE sport = :sport ORDER BY position")
    fun loadByPosition(sport: Sport): Flow<List<Ranking>>

    @Query("SELECT * FROM rankings WHERE sport = :sport ORDER BY teamName")
    fun loadByTeamName(sport: Sport): Flow<List<Ranking>>

    @Query("SELECT teamId FROM rankings WHERE sport = :sport")
    suspend fun loadTeamIds(sport: Sport): List<String>
}
