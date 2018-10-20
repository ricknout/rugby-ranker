package com.ricknout.rugbyranker.work

import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkStatus
import com.ricknout.rugbyranker.vo.RankingsType
import java.util.concurrent.TimeUnit

class RugbyRankerWorkManager {

    private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    private val mensWorkRequest = PeriodicWorkRequest.Builder(
            MensWorldRugbyRankingsWorker::class.java, WORK_REQUEST_REPEAT_INTERVAL, WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT
    ).setConstraints(constraints).build()

    private val womensWorkRequest = PeriodicWorkRequest.Builder(
            WomensWorldRugbyRankingsWorker::class.java, WORK_REQUEST_REPEAT_INTERVAL, WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT
    ).setConstraints(constraints).build()

    fun fetchAndStoreLatestWorldRugbyRankings(rankingsType: RankingsType) {
        val uniqueWorkName = getUniqueWorkName(rankingsType)
        val workRequest = when (rankingsType) {
            RankingsType.MENS -> mensWorkRequest
            RankingsType.WOMENS -> womensWorkRequest
        }
        val workManager = WorkManager.getInstance()
        workManager.enqueueUniquePeriodicWork(uniqueWorkName, WORK_REQUEST_EXISTING_PERIODIC_WORK_POLICY, workRequest)
    }

    fun getLatestWorldRugbyRankingsStatuses(rankingsType: RankingsType): LiveData<List<WorkStatus>> {
        val uniqueWorkName = getUniqueWorkName(rankingsType)
        val workManager = WorkManager.getInstance()
        return workManager.getStatusesForUniqueWorkLiveData(uniqueWorkName)
    }

    private fun getUniqueWorkName(rankingsType: RankingsType) = when (rankingsType) {
        RankingsType.MENS -> MensWorldRugbyRankingsWorker.UNIQUE_WORK_NAME
        RankingsType.WOMENS -> WomensWorldRugbyRankingsWorker.UNIQUE_WORK_NAME
    }

    companion object {
        private const val WORK_REQUEST_REPEAT_INTERVAL = 1L
        private val WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT = TimeUnit.DAYS
        private val WORK_REQUEST_EXISTING_PERIODIC_WORK_POLICY = ExistingPeriodicWorkPolicy.KEEP
    }
}
