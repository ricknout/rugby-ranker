package dev.ricknout.rugbyranker.prediction.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Prediction(
    val id: String,
    val homeTeam: Team,
    val homeScore: Int,
    val awayTeam: Team,
    val awayScore: Int,
    val rugbyWorldCup: Boolean,
    val noHomeAdvantage: Boolean,
) : Parcelable {
    companion object {
        fun generateId() = System.currentTimeMillis().toString()
    }
}
