package com.ricknout.rugbyranker.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object RugbyRankerMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE world_rugby_rankings_new (teamId INTEGER PRIMARY KEY NOT NULL, teamName TEXT NOT NULL, teamAbbreviation TEXT NOT NULL, position INTEGER NOT NULL, previousPosition INTEGER NOT NULL, points REAL NOT NULL, previousPoints REAL NOT NULL, matches INTEGER NOT NULL, sport INTEGER NOT NULL)")
            database.execSQL("INSERT INTO world_rugby_rankings_new SELECT teamId, teamName, teamAbbreviation, position, previousPosition, points, previousPoints, matches, rankingsType FROM world_rugby_rankings")
            database.execSQL("DROP TABLE world_rugby_rankings")
            database.execSQL("ALTER TABLE world_rugby_rankings_new RENAME TO world_rugby_rankings")
        }
    }
}