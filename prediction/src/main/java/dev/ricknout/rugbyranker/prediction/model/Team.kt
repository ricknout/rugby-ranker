package dev.ricknout.rugbyranker.prediction.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Team(
    val id: Long,
    val name: String,
    val abbreviation: String,
) : Parcelable
