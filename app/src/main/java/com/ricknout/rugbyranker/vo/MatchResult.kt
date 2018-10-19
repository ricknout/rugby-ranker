package com.ricknout.rugbyranker.vo

import java.util.UUID

data class MatchResult(
        val id: String,
        val homeTeamId: Long,
        val homeTeamName: String,
        val homeTeamAbbreviation: String,
        val homeTeamScore: Int,
        val awayTeamId: Long,
        val awayTeamName: String,
        val awayTeamAbbreviation: String,
        val awayTeamScore: Int,
        val noHomeAdvantage: Boolean,
        val rugbyWorldCup: Boolean,
        val isEditing: Boolean = false
) {
    companion object {
        fun generateId() = UUID.randomUUID().toString()
    }
}
