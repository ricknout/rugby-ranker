package com.ricknout.worldrugbyranker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ricknout.worldrugbyranker.vo.WorldRugbyRanking

@Database(
        entities = [WorldRugbyRanking::class],
        version = 1,
        exportSchema = false
)
@TypeConverters(WorldRugbyRankerTypeConverters::class)
abstract class WorldRugbyRankerDb : RoomDatabase() {

    abstract fun worldRugbyRankingDao(): WorldRugbyRankingDao
}
