package dev.ricknout.rugbyranker.news.model

data class News(
    val id: String,
    val type: Type,
    val title: String,
    val subtitle: String?,
    val summary: String,
    val imageUrl: String?,
    val articleUrl: String,
    val timeMillis: Long,
    val language: String,
)
