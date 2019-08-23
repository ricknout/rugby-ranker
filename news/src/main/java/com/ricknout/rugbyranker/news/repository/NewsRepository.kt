package com.ricknout.rugbyranker.news.repository

import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.ricknout.rugbyranker.core.api.WorldRugbyService
import com.ricknout.rugbyranker.news.db.WorldRugbyNewsDao
import com.ricknout.rugbyranker.news.vo.NewsDataConverter
import com.ricknout.rugbyranker.news.vo.WorldRugbyArticle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsRepository(
    private val worldRugbyService: WorldRugbyService,
    private val worldRugbyNewsDao: WorldRugbyNewsDao
) {

    fun loadLatestWorldRugbyNews(): LiveData<PagedList<WorldRugbyArticle>> {
        val dataSourceFactory = worldRugbyNewsDao.load()
        val config = Config(pageSize = PAGE_SIZE_WORLD_RUGBY_NEWS_DATABASE, enablePlaceholders = false)
        return dataSourceFactory.toLiveData(config = config)
    }

    suspend fun fetchAndCacheLatestWorldRugbyNewsSync(pageSize: Int = PAGE_SIZE_WORLD_RUGBY_NEWS_NETWORK): Boolean {
        val language = WorldRugbyService.LANGUAGE_EN
        val tagNames = WorldRugbyService.TAG_NAME_NEWS
        val page = 0
        return try {
            val worldRugbyNewsResponse = worldRugbyService.getNews(language, tagNames, page, pageSize)
            val worldRugbyArticles = NewsDataConverter.getWorldRugbyArticlesFromWorldRugbyNewsResponse(worldRugbyNewsResponse)
            worldRugbyNewsDao.insert(worldRugbyArticles)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun fetchAndCacheLatestWorldRugbyNewsAsync(coroutineScope: CoroutineScope, onComplete: (success: Boolean) -> Unit) {
        coroutineScope.launch {
            val success = withContext(Dispatchers.IO) {
                fetchAndCacheLatestWorldRugbyNewsSync(pageSize = PAGE_SIZE_WORLD_RUGBY_NEWS_NETWORK_REFRESH)
            }
            onComplete(success)
        }
    }

    companion object {
        private const val PAGE_SIZE_WORLD_RUGBY_NEWS_DATABASE = 10
        private const val PAGE_SIZE_WORLD_RUGBY_NEWS_NETWORK = 100
        private const val PAGE_SIZE_WORLD_RUGBY_NEWS_NETWORK_REFRESH = 10
    }
}
