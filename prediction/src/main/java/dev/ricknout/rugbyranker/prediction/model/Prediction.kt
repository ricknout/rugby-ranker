package dev.ricknout.rugbyranker.prediction.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Parcelize
data class Prediction(
    val id: String,
    val homeTeam: Team,
    val homeScore: Int,
    val awayTeam: Team,
    val awayScore: Int,
    val rugbyWorldCup: Boolean,
    val noHomeAdvantage: Boolean
) : Parcelable {

    companion object {
        fun generateId() = UUID.randomUUID().toString()
    }
}
