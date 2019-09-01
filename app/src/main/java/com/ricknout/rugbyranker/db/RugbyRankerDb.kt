package com.ricknout.rugbyranker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ricknout.rugbyranker.matches.db.WorldRugbyMatchDao
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch
import com.ricknout.rugbyranker.news.db.WorldRugbyArticleDao
import com.ricknout.rugbyranker.news.vo.WorldRugbyArticle
import com.ricknout.rugbyranker.rankings.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.rankings.vo.WorldRugbyRanking
import com.ricknout.rugbyranker.teams.db.WorldRugbyTeamDao
import com.ricknout.rugbyranker.teams.vo.WorldRugbyTeam

@Database(
        entities = [
            WorldRugbyRanking::class,
            WorldRugbyMatch::class,
            WorldRugbyTeam::class,
            WorldRugbyArticle::class
        ],
        version = 5,
        exportSchema = false
)
@TypeConverters(RugbyRankerTypeConverters::class)
abstract class RugbyRankerDb : RoomDatabase() {

    abstract fun worldRugbyRankingDao(): WorldRugbyRankingDao

    abstract fun worldRugbyMatchDao(): WorldRugbyMatchDao

    abstract fun worldRugbyTeamDao(): WorldRugbyTeamDao

    abstract fun worldRugbyArticleDao(): WorldRugbyArticleDao

    companion object {
        const val DATABASE_NAME = "rugby_ranker.db"
    }
}
