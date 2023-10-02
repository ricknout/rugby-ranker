package dev.ricknout.rugbyranker.news.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.ricknout.rugbyranker.core.api.WorldRugbyService
import dev.ricknout.rugbyranker.news.model.News
import dev.ricknout.rugbyranker.news.model.Type
import kotlinx.coroutines.flow.Flow

class NewsRepository(private val service: WorldRugbyService) {
    fun loadNews(type: Type): Flow<PagingData<News>> {
        return Pager(
            config =
                PagingConfig(
                    pageSize = PAGE_SIZE,
                    initialLoadSize = PAGE_SIZE,
                ),
            pagingSourceFactory = { NewsPagingSource(type, this) },
        ).flow
    }

    suspend fun fetchLatestNewsSync(
        type: Type,
        page: Int,
        pageSize: Int,
    ): Pair<Boolean, List<News>> {
        val t =
            when (type) {
                Type.TEXT -> WorldRugbyService.TYPE_TEXT
            }
        val language = WorldRugbyService.LANGUAGE_EN
        val tagNames = WorldRugbyService.TAG_NAME_NEWS
        return try {
            val response = service.getArticles(t, language, tagNames, page, pageSize)
            val news = NewsDataConverter.getNewsFromResponse(response)
            true to news
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            false to emptyList()
        }
    }

    companion object {
        private const val TAG = "NewsRepository"
        private const val PAGE_SIZE = 10
    }
}
