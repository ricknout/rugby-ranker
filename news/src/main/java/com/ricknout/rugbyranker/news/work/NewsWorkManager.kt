package com.ricknout.rugbyranker.news.work

import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class NewsWorkManager(private val workManager: WorkManager) {

    private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    private val newsWorkRequest = PeriodicWorkRequestBuilder<WorldRugbyNewsWorker>(
            WORK_REQUEST_REPEAT_INTERVAL, WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT
    ).setConstraints(constraints).build()

    fun fetchAndStoreLatestWorldRugbyNews() {
        val uniqueWorkName = getNewsUniqueWorkName()
        val workRequest = newsWorkRequest
        workManager.enqueueUniquePeriodicWork(uniqueWorkName, WORK_REQUEST_EXISTING_PERIODIC_WORK_POLICY, workRequest)
    }

    fun getLatestWorldRugbyNewsWorkInfos(): LiveData<List<WorkInfo>> {
        val uniqueWorkName = getNewsUniqueWorkName()
        return workManager.getWorkInfosForUniqueWorkLiveData(uniqueWorkName)
    }

    private fun getNewsUniqueWorkName() = WorldRugbyNewsWorker.UNIQUE_WORK_NAME

    companion object {
        private const val WORK_REQUEST_REPEAT_INTERVAL = 1L
        private val WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT = TimeUnit.DAYS
        private val WORK_REQUEST_EXISTING_PERIODIC_WORK_POLICY = ExistingPeriodicWorkPolicy.KEEP
    }
}
