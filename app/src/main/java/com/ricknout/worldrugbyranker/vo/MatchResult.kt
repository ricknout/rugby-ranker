package com.ricknout.worldrugbyranker.vo

data class MatchResult(
        val homeTeamId: Long,
        val homeTeamAbbreviation: String,
        val homeTeamScore: Int,
        val awayTeamId: Long,
        val awayTeamAbbreviation: String,
        val awayTeamScore: Int,
        val noHomeAdvantage: Boolean,
        val rugbyWorldCup: Boolean
)
