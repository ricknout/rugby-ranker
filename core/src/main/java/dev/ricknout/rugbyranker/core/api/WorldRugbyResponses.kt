package dev.ricknout.rugbyranker.core.api

data class Effective(
    val label: String,
    val millis: Long,
    val gmtOffset: Float,
)

data class Team(
    val id: String,
    val name: String,
    val abbreviation: String?,
)

val tbc = Team(id = "TBC", name = "TBC", abbreviation = "TBC")

data class Entry(
    val pos: Int,
    val previousPos: Int,
    val pts: Float,
    val previousPts: Float,
    val matches: Int,
    val team: Team,
)

data class WorldRugbyRankingsResponse(
    val effective: Effective,
    val entries: List<Entry>,
    val label: String,
)

data class PageInfo(
    val page: Int,
    val numPages: Int,
    val pageSize: Int,
    val numEntries: Int,
)

data class Venue(
    val id: String,
    val name: String,
    val city: String,
    val country: String,
)

data class Clock(
    val secs: Int,
    val label: String,
)

data class Event(
    val id: String,
    val label: String,
    val sport: String,
    val start: Effective,
    val end: Effective,
    val rankingsWeight: Float,
)

data class Content(
    val matchId: String,
    val description: String?,
    val venue: Venue?,
    val time: Effective,
    val attendance: Int,
    val teams: List<Team?>,
    val scores: List<Int>,
    val status: String,
    val clock: Clock?,
    val events: List<Event>,
)

data class WorldRugbyMatchesResponse(
    val pageInfo: PageInfo,
    val content: List<Content>,
)

data class WorldRugbyMatchSummaryResponse(
    val match: Content,
)

data class Article(
    val id: String,
    val type: String,
    val title: String,
    val description: String?,
    val publishFrom: Long,
    val language: String,
    val canonicalUrl: String,
    val subtitle: String?,
    val summary: String?,
    val imageUrl: String?,
    val onDemandUrl: String?,
)

data class WorldRugbyNewsResponse(
    val pageInfo: PageInfo,
    val content: List<Article>,
)
