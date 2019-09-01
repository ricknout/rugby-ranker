package com.ricknout.rugbyranker.news.work

import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.ricknout.rugbyranker.news.vo.ArticleType
import java.util.concurrent.TimeUnit

class ArticlesWorkManager(private val workManager: WorkManager) {

    private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    private val textArticlesWorkRequest = PeriodicWorkRequestBuilder<TextWorldRugbyArticlesWorker>(
            WORK_REQUEST_REPEAT_INTERVAL, WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT
    ).setConstraints(constraints).build()

    private val videoArticlesWorkRequest = PeriodicWorkRequestBuilder<VideoWorldRugbyArticlesWorker>(
            WORK_REQUEST_REPEAT_INTERVAL, WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT
    ).setConstraints(constraints).build()

    fun fetchAndStoreLatestWorldRugbyArticles(articleType: ArticleType) {
        val uniqueWorkName = getArticlesUniqueWorkName(articleType)
        val workRequest = when (articleType) {
            ArticleType.TEXT -> textArticlesWorkRequest
            ArticleType.VIDEO -> videoArticlesWorkRequest
        }
        workManager.enqueueUniquePeriodicWork(uniqueWorkName, WORK_REQUEST_EXISTING_PERIODIC_WORK_POLICY, workRequest)
    }

    fun getLatestWorldRugbyArticlesWorkInfos(articleType: ArticleType): LiveData<List<WorkInfo>> {
        val uniqueWorkName = getArticlesUniqueWorkName(articleType)
        return workManager.getWorkInfosForUniqueWorkLiveData(uniqueWorkName)
    }

    private fun getArticlesUniqueWorkName(articleType: ArticleType) = when (articleType) {
        ArticleType.TEXT -> TextWorldRugbyArticlesWorker.UNIQUE_WORK_NAME
        ArticleType.VIDEO -> VideoWorldRugbyArticlesWorker.UNIQUE_WORK_NAME
    }

    companion object {
        private const val WORK_REQUEST_REPEAT_INTERVAL = 1L
        private val WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT = TimeUnit.DAYS
        private val WORK_REQUEST_EXISTING_PERIODIC_WORK_POLICY = ExistingPeriodicWorkPolicy.KEEP
    }
}
