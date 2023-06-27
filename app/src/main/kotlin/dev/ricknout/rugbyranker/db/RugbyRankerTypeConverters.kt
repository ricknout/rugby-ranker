package dev.ricknout.rugbyranker.db

import androidx.room.TypeConverter
import dev.ricknout.rugbyranker.core.model.Sport

class RugbyRankerTypeConverters {

    @TypeConverter
    fun ordinalToSport(ordinal: Int): Sport = Sport.values()[ordinal]

    @TypeConverter
    fun sportToOrdinal(sport: Sport): Int = sport.ordinal
}
