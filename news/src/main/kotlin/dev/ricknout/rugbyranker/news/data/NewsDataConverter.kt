package dev.ricknout.rugbyranker.news.data

import dev.ricknout.rugbyranker.core.api.Article
import dev.ricknout.rugbyranker.core.api.WorldRugbyNewsResponse
import dev.ricknout.rugbyranker.core.api.WorldRugbyService
import dev.ricknout.rugbyranker.news.model.News
import dev.ricknout.rugbyranker.news.model.Type

object NewsDataConverter {

    fun getNewsFromResponse(response: WorldRugbyNewsResponse): List<News> {
        return response.content.map { article ->
            News(
                id = article.id,
                type = getTypeFromResponse(article),
                title = article.title,
                subtitle = article.subtitle,
                summary = when {
                    !article.description.isNullOrEmpty() -> article.description!!
                    !article.summary.isNullOrEmpty() -> article.summary!!
                    else -> article.title
                },
                imageUrl = if (!article.onDemandUrl.isNullOrEmpty()) {
                    "${article.onDemandUrl}?width=$WIDTH_IMAGE"
                } else {
                    article.imageUrl
                },
                articleUrl = article.canonicalUrl,
                timeMillis = article.publishFrom,
                language = article.language,
            )
        }
    }

    private fun getTypeFromResponse(article: Article): Type {
        return when (article.type) {
            WorldRugbyService.TYPE_TEXT -> Type.TEXT
            else -> throw IllegalArgumentException("Unknown article type ${article.type}")
        }
    }

    private const val WIDTH_IMAGE = 1000
}
