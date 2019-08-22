package com.ricknout.rugbyranker.news.vo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "world_rugby_articles")
data class WorldRugbyArticle(
    @PrimaryKey
    val id: Long,
    val title: String,
    val subtitle: String,
    val summary: String,
    val imageUrl: String,
    val articleUrl: String,
    val timeMillis: Long,
    val language: String
)
