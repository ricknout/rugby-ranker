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
                    imageUrl = article.imageUrl,
                    articleUrl = article.canonicalUrl,
                    timeMillis = article.publishFrom,
                    language = article.language
            )
        }
    }
}
