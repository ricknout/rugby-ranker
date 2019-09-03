package com.ricknout.rugbyranker.news.repository

import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.ricknout.rugbyranker.core.api.WorldRugbyService
import com.ricknout.rugbyranker.news.db.WorldRugbyArticleDao
import com.ricknout.rugbyranker.news.prefs.ArticlesSharedPreferences
import com.ricknout.rugbyranker.news.vo.ArticleType
import com.ricknout.rugbyranker.news.vo.ArticlesDataConverter
import com.ricknout.rugbyranker.news.vo.WorldRugbyArticle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ArticlesRepository(
    private val worldRugbyService: WorldRugbyService,
    private val worldRugbyArticleDao: WorldRugbyArticleDao,
    private val articlesSharedPreferences: ArticlesSharedPreferences
) {

    fun loadLatestWorldRugbyArticles(articleType: ArticleType): LiveData<PagedList<WorldRugbyArticle>> {
        val dataSourceFactory = worldRugbyArticleDao.load(articleType)
        val config = Config(pageSize = PAGE_SIZE_WORLD_RUGBY_ARTICLES_DATABASE, enablePlaceholders = false)
        return dataSourceFactory.toLiveData(config = config)
    }

    fun isInitialArticlesFetched(articleType: ArticleType) =
            articlesSharedPreferences.isInitialArticlesFetched(articleType)

    suspend fun fetchAndCacheLatestWorldRugbyArticlesSync(
        articleType: ArticleType,
        pageSize: Int = PAGE_SIZE_WORLD_RUGBY_ARTICLES_NETWORK,
        fetchMultiplePages: Boolean = true
    ): Boolean {
        val type = when (articleType) {
            ArticleType.TEXT -> WorldRugbyService.TYPE_TEXT
            ArticleType.VIDEO -> WorldRugbyService.TYPE_VIDEO
        }
        val language = WorldRugbyService.LANGUAGE_EN
        val tagNames = when (articleType) {
            ArticleType.TEXT -> WorldRugbyService.TAG_NAME_NEWS
            ArticleType.VIDEO -> ""
        }
        var page = 0
        var pageCount = Int.MAX_VALUE
        var success = false
        val initialArticlesFetched = isInitialArticlesFetched(articleType)
        return try {
            while (page < pageCount) {
                val worldRugbyArticlesResponse = worldRugbyService.getArticles(type, language, tagNames, page, pageSize)
                val worldRugbyArticles = ArticlesDataConverter.getWorldRugbyArticlesFromWorldRugbyArticlesResponse(worldRugbyArticlesResponse)
                worldRugbyArticleDao.insert(worldRugbyArticles)
                page++
                pageCount = if (fetchMultiplePages && !initialArticlesFetched) worldRugbyArticlesResponse.pageInfo.numPages.coerceAtMost(MAX_PAGES_WORLD_RUGBY_ARTICLES_NETWORK) else 1
                success = true
            }
            if (fetchMultiplePages) articlesSharedPreferences.setInitialArticlesFetched(articleType, true)
            success
        } catch (_: Exception) {
            success
        }
    }

    fun fetchAndCacheLatestWorldRugbyArticlesAsync(articleType: ArticleType, coroutineScope: CoroutineScope, onComplete: (success: Boolean) -> Unit) {
        coroutineScope.launch {
            val success = withContext(Dispatchers.IO) {
                fetchAndCacheLatestWorldRugbyArticlesSync(articleType, fetchMultiplePages = false)
            }
            onComplete(success)
        }
    }

    companion object {
        private const val PAGE_SIZE_WORLD_RUGBY_ARTICLES_DATABASE = 20
        private const val PAGE_SIZE_WORLD_RUGBY_ARTICLES_NETWORK = 10
        private const val MAX_PAGES_WORLD_RUGBY_ARTICLES_NETWORK = 10
    }
}
