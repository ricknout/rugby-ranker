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
            database.execSQL("CREATE TABLE world_rugby_matches (matchId INTEGER PRIMARY KEY NOT NULL, description TEXT, status INTEGER NOT NULL, attendance INTEGER NOT NULL, firstTeamId INTEGER NOT NULL, firstTeamName TEXT NOT NULL, firstTeamAbbreviation TEXT, firstTeamScore INTEGER NOT NULL, secondTeamId INTEGER NOT NULL, secondTeamName TEXT NOT NULL, secondTeamAbbreviation TEXT, secondTeamScore INTEGER NOT NULL, timeLabel TEXT NOT NULL, timeMillis INTEGER NOT NULL, timeGmtOffset INTEGER NOT NULL, venueId INTEGER, venueName TEXT, venueCity TEXT, venueCountry TEXT, eventId INTEGER, eventLabel TEXT, eventSport INTEGER NOT NULL, eventRankingsWeight REAL, eventStartTimeLabel TEXT, eventStartTimeMillis INTEGER, eventStartTimeGmtOffset INTEGER, eventEndTimeLabel TEXT, eventEndTimeMillis INTEGER, eventEndTimeGmtOffset INTEGER)")
        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE world_rugby_teams (id INTEGER PRIMARY KEY NOT NULL, name TEXT NOT NULL, abbreviation TEXT NOT NULL, sport INTEGER NOT NULL)")
        }
    }
}
