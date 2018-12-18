package com.ricknout.rugbyranker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ricknout.rugbyranker.matches.db.WorldRugbyMatchDao
import com.ricknout.rugbyranker.rankings.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch
import com.ricknout.rugbyranker.rankings.vo.WorldRugbyRanking

@Database(
        entities = [WorldRugbyRanking::class, WorldRugbyMatch::class],
        version = 2,
        exportSchema = false
)
@TypeConverters(RugbyRankerTypeConverters::class)
abstract class RugbyRankerDb : RoomDatabase() {

    abstract fun worldRugbyRankingDao(): WorldRugbyRankingDao

    abstract fun worldRugbyMatchDao(): WorldRugbyMatchDao

    companion object {
        const val DATABASE_NAME = "rugby_ranker.db"
    }
}
