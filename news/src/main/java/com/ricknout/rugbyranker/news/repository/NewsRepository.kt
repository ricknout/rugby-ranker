package com.ricknout.rugbyranker.news.repository

import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.ricknout.rugbyranker.core.api.WorldRugbyService
import com.ricknout.rugbyranker.news.db.WorldRugbyNewsDao
import com.ricknout.rugbyranker.news.prefs.NewsSharedPreferences
import com.ricknout.rugbyranker.news.vo.NewsDataConverter
import com.ricknout.rugbyranker.news.vo.WorldRugbyArticle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsRepository(
    private val worldRugbyService: WorldRugbyService,
    private val worldRugbyNewsDao: WorldRugbyNewsDao,
    private val newsSharedPreferences: NewsSharedPreferences
) {

    fun loadLatestWorldRugbyNews(): LiveData<PagedList<WorldRugbyArticle>> {
        val dataSourceFactory = worldRugbyNewsDao.load()
        val config = Config(pageSize = PAGE_SIZE_WORLD_RUGBY_NEWS_DATABASE, enablePlaceholders = false)
        return dataSourceFactory.toLiveData(config = config)
    }

    suspend fun fetchAndCacheLatestWorldRugbyNewsSync(
        pageSize: Int = PAGE_SIZE_WORLD_RUGBY_NEWS_NETWORK,
        fetchMultiplePages: Boolean = true
    ): Boolean {
        val language = WorldRugbyService.LANGUAGE_EN
        val tagNames = WorldRugbyService.TAG_NAME_NEWS
        var page = 0
        var pageCount = Int.MAX_VALUE
        var success = false
        val initialNewsFetched = newsSharedPreferences.isInitialNewsFetched()
        while (page < pageCount) {
            try {
                val worldRugbyNewsResponse = worldRugbyService.getNews(language, tagNames, page, pageSize)
                val worldRugbyArticles = NewsDataConverter.getWorldRugbyArticlesFromWorldRugbyNewsResponse(worldRugbyNewsResponse)
                worldRugbyNewsDao.insert(worldRugbyArticles)
                page++
                pageCount = if (fetchMultiplePages && !initialNewsFetched) {
                    worldRugbyNewsResponse.pageInfo.numPages.coerceAtMost(MAX_PAGES_WORLD_RUGBY_NEWS_NETWORK)
                } else {
                    1
                }
                success = true
            } catch (_: Exception) {
                // If we have successfully loaded other pages of articles, do not consider this a failure
            }
        }
        if (fetchMultiplePages) newsSharedPreferences.setInitialNewsFetched(true)
        return success
    }

    fun fetchAndCacheLatestWorldRugbyNewsAsync(coroutineScope: CoroutineScope, onComplete: (success: Boolean) -> Unit) {
        coroutineScope.launch {
            val success = withContext(Dispatchers.IO) {
                fetchAndCacheLatestWorldRugbyNewsSync(pageSize = PAGE_SIZE_WORLD_RUGBY_NEWS_NETWORK_REFRESH, fetchMultiplePages = false)
            }
            onComplete(success)
        }
    }

    companion object {
        private const val PAGE_SIZE_WORLD_RUGBY_NEWS_DATABASE = 10
        private const val PAGE_SIZE_WORLD_RUGBY_NEWS_NETWORK = 100
        private const val PAGE_SIZE_WORLD_RUGBY_NEWS_NETWORK_REFRESH = 10
        private const val MAX_PAGES_WORLD_RUGBY_NEWS_NETWORK = 5
    }
}
