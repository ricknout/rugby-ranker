package dev.ricknout.rugbyranker.ranking.work

import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dev.ricknout.rugbyranker.core.model.Sport
import java.util.concurrent.TimeUnit

class RankingWorkManager(private val workManager: WorkManager) {
    private val constraints =
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    private val mensWork =
        PeriodicWorkRequestBuilder<MensRankingWorker>(
            REPEAT_INTERVAL,
            REPEAT_INTERVAL_TIME_UNIT,
        ).setConstraints(constraints).build()

    private val womensWork =
        PeriodicWorkRequestBuilder<WomensRankingWorker>(
            REPEAT_INTERVAL,
            REPEAT_INTERVAL_TIME_UNIT,
        ).setConstraints(constraints).build()

    fun enqueueWork(sport: Sport) {
        val uniqueWorkName = getUniqueWorkName(sport)
        val work =
            when (sport) {
                Sport.MENS -> mensWork
                Sport.WOMENS -> womensWork
            }
        workManager.enqueueUniquePeriodicWork(uniqueWorkName, EXISTING_PERIODIC_WORK_POLICY, work)
    }

    fun getWorkInfos(sport: Sport): LiveData<List<WorkInfo>> {
        val uniqueWorkName = getUniqueWorkName(sport)
        return workManager.getWorkInfosForUniqueWorkLiveData(uniqueWorkName)
    }

    private fun getUniqueWorkName(sport: Sport) =
        when (sport) {
            Sport.MENS -> MensRankingWorker.UNIQUE_WORK_NAME
            Sport.WOMENS -> WomensRankingWorker.UNIQUE_WORK_NAME
        }

    companion object {
        private const val REPEAT_INTERVAL = 1L
        private val REPEAT_INTERVAL_TIME_UNIT = TimeUnit.DAYS
        private val EXISTING_PERIODIC_WORK_POLICY = ExistingPeriodicWorkPolicy.UPDATE
    }
}
