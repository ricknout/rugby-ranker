package dev.ricknout.rugbyranker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.ricknout.rugbyranker.core.db.RankingDao
import dev.ricknout.rugbyranker.core.model.Ranking

@Database(
    entities = [Ranking::class],
    version = 2,
)
@TypeConverters(RugbyRankerTypeConverters::class)
abstract class RugbyRankerDatabase : RoomDatabase() {
    abstract fun rankingDao(): RankingDao

    companion object {
        const val DATABASE_NAME = "rugby_ranker_database"
    }
}
