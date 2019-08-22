package com.ricknout.rugbyranker.core.api

data class Effective(
    val label: String,
    val millis: Long,
    val gmtOffset: Float
)

data class Team(
    val id: Long,
    val name: String,
    val abbreviation: String?
)

data class Entry(
    val pos: Int,
    val previousPos: Int,
    val pts: Float,
    val previousPts: Float,
    val matches: Int,
    val team: Team
)

data class WorldRugbyRankingsResponse(
    val effective: Effective,
    val entries: List<Entry>,
    val label: String
)

data class PageInfo(
    val page: Int,
    val numPages: Int,
    val pageSize: Int,
    val numEntries: Int
)

data class Venue(
    val id: Long,
    val name: String,
    val city: String,
    val country: String
)

data class Event(
    val id: Long,
    val label: String,
    val sport: String,
    val start: Effective,
    val end: Effective,
    val rankingsWeight: Float
)

data class Match(
    val matchId: Long,
    val description: String?,
    val venue: Venue?,
    val time: Effective,
    val attendance: Int,
    val teams: List<Team>,
    val scores: List<Int>,
    val status: String,
    val events: List<Event>
)

data class WorldRugbyMatchesResponse(
    val pageInfo: PageInfo,
    val content: List<Match>
)

data class TeamDetail(
    val id: Long,
    val country: String,
    val naming: Naming
)

data class Naming(
    val name: String,
    val abbr: String
)

data class WorldRugbyTeamsResponse(
    val teams: List<TeamDetail>
)

data class Article(
    val id: Long,
    val title: String,
    val description: String,
    val publishFrom: Long,
    val language: String,
    val canonicalUrl: String,
    val subtitle: String,
    val imageUrl: String,
    val onDemandUrl: String?
)

data class WorldRugbyNewsResponse(
    val pageInfo: PageInfo,
    val content: List<Article>
)
