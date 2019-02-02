package com.ricknout.rugbyranker.db

import androidx.room.TypeConverter
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.matches.vo.MatchHalf

class RugbyRankerTypeConverters {

    @TypeConverter
    fun ordinalToSport(ordinal: Int): Sport = Sport.values()[ordinal]

    @TypeConverter
    fun sportToOrdinal(sport: Sport): Int = sport.ordinal

    @TypeConverter
    fun ordinalToMatchStatus(ordinal: Int): MatchStatus = MatchStatus.values()[ordinal]

    @TypeConverter
    fun matchStatusToOrdinal(matchStatus: MatchStatus): Int = matchStatus.ordinal

    @TypeConverter
    fun ordinalToMatchHalf(ordinal: Int): MatchHalf? = if (ordinal == -1) null else MatchHalf.values()[ordinal]

    @TypeConverter
    fun matchHalfToOrdinal(matchHalf: MatchHalf?): Int = if (matchHalf == null) -1 else matchHalf.ordinal
}
