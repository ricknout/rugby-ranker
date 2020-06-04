package dev.ricknout.rugbyranker.news.data

import androidx.paging.PagingSource
import dev.ricknout.rugbyranker.news.model.News
import dev.ricknout.rugbyranker.news.model.Type

class NewsPagingSource(
    private val type: Type,
    private val repository: NewsRepository
) : PagingSource<Int, News>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, News> {
        val page = params.key ?: 0
        val pageSize = params.loadSize
        val (success, news) = repository.fetchLatestNewsSync(type, page, pageSize)
        return if (success) {
            LoadResult.Page(
                data = news,
                prevKey = if (page == 0) null else page.dec(),
                nextKey = if (news.isEmpty()) null else page.inc()
            )
        } else {
            LoadResult.Error(RuntimeException("Failed to fetch news with page = $page and page size = $pageSize"))
        }
    }
}
