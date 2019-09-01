package com.ricknout.rugbyranker.db

import androidx.room.TypeConverter
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.news.vo.ArticleType

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
    fun ordinalToArticleType(ordinal: Int): ArticleType = ArticleType.values()[ordinal]

    @TypeConverter
    fun articleTypeToOrdinal(articleType: ArticleType): Int = articleType.ordinal
}
