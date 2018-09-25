package com.ricknout.worldrugbyranker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ricknout.worldrugbyranker.vo.MensWorldRugbyRanking
import com.ricknout.worldrugbyranker.vo.WomensWorldRugbyRanking
import com.ricknout.worldrugbyranker.vo.WorldRugbyRanking

@Database(
        entities = [MensWorldRugbyRanking::class, WomensWorldRugbyRanking::class],
        version = 1,
        exportSchema = false
)
abstract class WorldRugbyRankerDb : RoomDatabase() {

    abstract fun worldRugbyRankingDao(): WorldRugbyRankingDao
}
