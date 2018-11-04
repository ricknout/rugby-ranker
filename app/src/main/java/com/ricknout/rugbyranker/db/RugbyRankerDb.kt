package com.ricknout.rugbyranker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ricknout.rugbyranker.vo.WorldRugbyMatch
import com.ricknout.rugbyranker.vo.WorldRugbyRanking

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
