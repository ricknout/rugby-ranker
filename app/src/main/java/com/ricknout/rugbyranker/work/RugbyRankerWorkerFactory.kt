package com.ricknout.rugbyranker.work

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.repository.RugbyRankerRepository
import javax.inject.Inject

class RugbyRankerWorkerFactory @Inject constructor(private val rugbyRankerRepository: RugbyRankerRepository) : WorkerFactory() {

    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {

        return when (workerClassName) {
            MensWorldRugbyRankingsWorker::class.java.name -> MensWorldRugbyRankingsWorker(
                    appContext, workerParameters, rugbyRankerRepository
            )
            WomensWorldRugbyRankingsWorker::class.java.name -> WomensWorldRugbyRankingsWorker(
                    appContext, workerParameters, rugbyRankerRepository
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
