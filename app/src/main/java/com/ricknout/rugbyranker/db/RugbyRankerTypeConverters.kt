package com.ricknout.rugbyranker.db

import androidx.room.TypeConverter
import com.ricknout.rugbyranker.vo.MatchStatus
import com.ricknout.rugbyranker.common.vo.Sport

class RugbyRankerTypeConverters {

    @TypeConverter
    fun ordinalToSport(ordinal: Int): Sport = Sport.values()[ordinal]

    @TypeConverter
    fun sportToOrdinal(sport: Sport): Int = sport.ordinal

    @TypeConverter
    fun ordinalToMatchStatus(ordinal: Int): MatchStatus = MatchStatus.values()[ordinal]

    @TypeConverter
    fun matchStatusToOrdinal(matchStatus: MatchStatus): Int = matchStatus.ordinal
}
