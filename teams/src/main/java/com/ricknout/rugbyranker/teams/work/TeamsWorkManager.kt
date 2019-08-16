package com.ricknout.rugbyranker.teams.work

import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.ricknout.rugbyranker.core.vo.Sport
import java.util.concurrent.TimeUnit

class TeamsWorkManager(private val workManager: WorkManager) {

    private val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    private val mensTeamsWorkRequest = PeriodicWorkRequestBuilder<MensWorldRugbyTeamsWorker>(
            WORK_REQUEST_REPEAT_INTERVAL, WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT
    ).setConstraints(constraints).build()

    private val womensTeamsWorkRequest = PeriodicWorkRequestBuilder<WomensWorldRugbyTeamsWorker>(
            WORK_REQUEST_REPEAT_INTERVAL, WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT
    ).setConstraints(constraints).build()

    fun fetchAndStoreLatestWorldRugbyTeams(sport: Sport) {
        val uniqueWorkName = getTeamsUniqueWorkName(sport)
        val workRequest = when (sport) {
            Sport.MENS -> mensTeamsWorkRequest
            Sport.WOMENS -> womensTeamsWorkRequest
        }
        workManager.enqueueUniquePeriodicWork(uniqueWorkName, WORK_REQUEST_EXISTING_PERIODIC_WORK_POLICY, workRequest)
    }

    fun getLatestWorldRugbyTeamsWorkInfos(sport: Sport): LiveData<List<WorkInfo>> {
        val uniqueWorkName = getTeamsUniqueWorkName(sport)
        return workManager.getWorkInfosForUniqueWorkLiveData(uniqueWorkName)
    }

    private fun getTeamsUniqueWorkName(sport: Sport) = when (sport) {
        Sport.MENS -> MensWorldRugbyTeamsWorker.UNIQUE_WORK_NAME
        Sport.WOMENS -> WomensWorldRugbyTeamsWorker.UNIQUE_WORK_NAME
    }

    companion object {
        private const val WORK_REQUEST_REPEAT_INTERVAL = 30L
        private val WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT = TimeUnit.DAYS
        private val WORK_REQUEST_EXISTING_PERIODIC_WORK_POLICY = ExistingPeriodicWorkPolicy.KEEP
    }
}
