package com.ricknout.worldrugbyranker.db

import androidx.room.TypeConverter
import com.ricknout.worldrugbyranker.vo.RankingsType

class WorldRugbyRankerTypeConverters {

    @TypeConverter
    fun ordinalToRankingsType(ordinal: Int): RankingsType = RankingsType.values()[ordinal]

    @TypeConverter
    fun rankingsTypeToOrdinal(rankingsType: RankingsType): Int = rankingsType.ordinal
}
