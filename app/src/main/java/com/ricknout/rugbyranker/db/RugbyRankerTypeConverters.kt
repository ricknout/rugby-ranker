package com.ricknout.rugbyranker.db

import androidx.room.TypeConverter
import com.ricknout.rugbyranker.vo.RankingsType

class RugbyRankerTypeConverters {

    @TypeConverter
    fun ordinalToRankingsType(ordinal: Int): RankingsType = RankingsType.values()[ordinal]

    @TypeConverter
    fun rankingsTypeToOrdinal(rankingsType: RankingsType): Int = rankingsType.ordinal
}
