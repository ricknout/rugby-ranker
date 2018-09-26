package com.ricknout.worldrugbyranker.vo

data class MatchResult(
        val homeTeamId: Long,
        val awayTeamId: Long,
        val homeTeamScore: Int,
        val awayTeamScore: Int,
        val noHomeAdvantage: Boolean,
        val rugbyWorldCup: Boolean
)
