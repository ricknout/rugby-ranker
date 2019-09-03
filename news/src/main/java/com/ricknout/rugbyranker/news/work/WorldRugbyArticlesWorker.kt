package com.ricknout.rugbyranker.news.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.news.repository.ArticlesRepository
import com.ricknout.rugbyranker.news.vo.ArticleType

open class WorldRugbyArticlesWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val articleType: ArticleType,
    private val articlesRepository: ArticlesRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork() = fetchAndCacheLatestWorldRugbyArticles()

    private suspend fun fetchAndCacheLatestWorldRugbyArticles(): Result {
        val success = articlesRepository.fetchAndCacheLatestWorldRugbyArticlesSync(articleType)
        return if (success) Result.success() else Result.retry()
    }
}
