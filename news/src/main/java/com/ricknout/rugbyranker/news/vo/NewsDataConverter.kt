package com.ricknout.rugbyranker.news.vo

import com.ricknout.rugbyranker.core.api.WorldRugbyNewsResponse

object NewsDataConverter {

    fun getWorldRugbyArticlesFromWorldRugbyNewsResponse(worldRugbyNewsResponse: WorldRugbyNewsResponse): List<WorldRugbyArticle> {
        return worldRugbyNewsResponse.content.map { article ->
            WorldRugbyArticle(
                    id = article.id,
                    title = article.title,
                    subtitle = article.subtitle,
                    summary = article.description,
                    imageUrl = if (article.onDemandUrl != null) "${article.onDemandUrl}?width=$WIDTH_IMAGE" else article.imageUrl,
                    articleUrl = article.canonicalUrl,
                    timeMillis = article.publishFrom,
                    language = article.language
            )
        }
    }

    private const val WIDTH_IMAGE = 500
}
