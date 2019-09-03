package com.ricknout.rugbyranker.news.vo

import com.ricknout.rugbyranker.core.api.Article
import com.ricknout.rugbyranker.core.api.WorldRugbyArticlesResponse
import com.ricknout.rugbyranker.core.api.WorldRugbyService

object ArticlesDataConverter {

    fun getWorldRugbyArticlesFromWorldRugbyArticlesResponse(worldRugbyArticlesResponse: WorldRugbyArticlesResponse): List<WorldRugbyArticle> {
        return worldRugbyArticlesResponse.content.map { article ->
            WorldRugbyArticle(
                    id = article.id,
                    type = getArticleTypeFromArticle(article),
                    title = article.title,
                    subtitle = article.subtitle,
                    summary = when {
                        !article.description.isNullOrEmpty() -> article.description!!
                        !article.summary.isNullOrEmpty() -> article.summary!!
                        else -> article.title
                    },
                    imageUrl = if (!article.onDemandUrl.isNullOrEmpty()) "${article.onDemandUrl}?width=$WIDTH_IMAGE" else article.imageUrl,
                    articleUrl = article.canonicalUrl,
                    timeMillis = article.publishFrom,
                    language = article.language
            )
        }
    }

    private fun getArticleTypeFromArticle(article: Article): ArticleType {
        return when (article.type) {
            WorldRugbyService.TYPE_TEXT -> ArticleType.TEXT
            WorldRugbyService.TYPE_VIDEO -> ArticleType.VIDEO
            else -> throw IllegalArgumentException("Unknown article type ${article.type}")
        }
    }

    private const val WIDTH_IMAGE = 500
}
