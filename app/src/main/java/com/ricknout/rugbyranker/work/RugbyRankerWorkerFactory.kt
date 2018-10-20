package com.ricknout.rugbyranker.work

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.api.WorldRugbyRankingsService
import com.ricknout.rugbyranker.db.WorldRugbyRankingDao
import javax.inject.Inject

class RugbyRankerWorkerFactory @Inject constructor(
        private val worldRugbyRankingsService: WorldRugbyRankingsService,
        private val worldRugbyRankingDao: WorldRugbyRankingDao
) : WorkerFactory() {

    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {

        return when (workerClassName) {
            MensWorldRugbyRankingsWorker::class.java.name -> MensWorldRugbyRankingsWorker(
                    appContext, workerParameters, worldRugbyRankingsService, worldRugbyRankingDao
            )
            WomensWorldRugbyRankingsWorker::class.java.name -> WomensWorldRugbyRankingsWorker(
                    appContext, workerParameters, worldRugbyRankingsService, worldRugbyRankingDao
            )
            else -> {
                try {
                    val clazz = Class.forName(workerClassName).asSubclass<Worker>(Worker::class.java)
                    val constructor = clazz.getDeclaredConstructor(Context::class.java, WorkerParameters::class.java)
                    val worker = constructor.newInstance(appContext, workerParameters)
                    worker
                } catch (e: Throwable) {
                    Log.e("RRWorkerFactory", "Could not instantiate $workerClassName", e)
                    null
                }
            }
        }
    }
}
