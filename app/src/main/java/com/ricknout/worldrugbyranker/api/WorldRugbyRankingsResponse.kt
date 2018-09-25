package com.ricknout.worldrugbyranker.api

data class Effective(
        val label: String,
        val millis: Long,
        val gmtOffset: Int
)

data class Team(
        val id: Long,
        val name: String,
        val abbreviation: String
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
