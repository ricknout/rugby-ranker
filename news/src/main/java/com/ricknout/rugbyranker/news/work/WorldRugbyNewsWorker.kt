package com.ricknout.rugbyranker.news.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.news.repository.NewsRepository
import javax.inject.Inject

class WorldRugbyNewsWorker @Inject constructor(
    context: Context,
    workerParams: WorkerParameters,
    private val newsRepository: NewsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork() = fetchAndCacheLatestWorldRugbyNews()

    private suspend fun fetchAndCacheLatestWorldRugbyNews(): Result {
        val success = newsRepository.fetchAndCacheLatestWorldRugbyNewsSync()
        return if (success) Result.success() else Result.retry()
    }

    companion object {
        const val UNIQUE_WORK_NAME = "world_rugby_news_worker"
    }
}
