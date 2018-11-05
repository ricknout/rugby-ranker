package com.ricknout.rugbyranker.work

import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkStatus
import com.ricknout.rugbyranker.vo.Sport
import java.util.concurrent.TimeUnit

class RugbyRankerWorkManager {

    private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    private val mensWorkRequest = PeriodicWorkRequestBuilder<MensWorldRugbyRankingsWorker>(
            WORK_REQUEST_REPEAT_INTERVAL, WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT
    ).setConstraints(constraints).build()

    private val womensWorkRequest = PeriodicWorkRequestBuilder<WomensWorldRugbyRankingsWorker>(
            WORK_REQUEST_REPEAT_INTERVAL, WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT
    ).setConstraints(constraints).build()

    fun fetchAndStoreLatestWorldRugbyRankings(sport: Sport) {
        val uniqueWorkName = getUniqueWorkName(sport)
        val workRequest = when (sport) {
            Sport.MENS -> mensWorkRequest
            Sport.WOMENS -> womensWorkRequest
        }
        val workManager = WorkManager.getInstance()
        workManager.enqueueUniquePeriodicWork(uniqueWorkName, WORK_REQUEST_EXISTING_PERIODIC_WORK_POLICY, workRequest)
    }

    fun getLatestWorldRugbyRankingsStatuses(sport: Sport): LiveData<List<WorkStatus>> {
        val uniqueWorkName = getUniqueWorkName(sport)
        val workManager = WorkManager.getInstance()
        return workManager.getStatusesForUniqueWorkLiveData(uniqueWorkName)
    }

    private fun getUniqueWorkName(sport: Sport) = when (sport) {
        Sport.MENS -> MensWorldRugbyRankingsWorker.UNIQUE_WORK_NAME
        Sport.WOMENS -> WomensWorldRugbyRankingsWorker.UNIQUE_WORK_NAME
    }

    companion object {
        private const val WORK_REQUEST_REPEAT_INTERVAL = 1L
        private val WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT = TimeUnit.DAYS
        private val WORK_REQUEST_EXISTING_PERIODIC_WORK_POLICY = ExistingPeriodicWorkPolicy.KEEP
    }
}
