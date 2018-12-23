package com.ricknout.rugbyranker.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.di.WorkerSubcomponent
import javax.inject.Inject
import javax.inject.Provider

class RugbyRankerWorkerFactory @Inject constructor(
        private val workerSubcomponent: WorkerSubcomponent.Builder
) : WorkerFactory() {

    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
        return workerSubcomponent
                .workerParameters(workerParameters)
                .build().run {
                    createWorker(workerClassName, workers())
                }
    }

    private fun createWorker(workerClassName: String, workers: Map<Class<out ListenableWorker>, Provider<ListenableWorker>>): ListenableWorker? {
        try {
            val workerClass = Class.forName(workerClassName).asSubclass(ListenableWorker::class.java)
            var provider = workers[workerClass]
            if (provider == null) {
                for ((key, value) in workers) {
                    if (workerClass.isAssignableFrom(key)) {
                        provider = value
                        break
                    }
                }
            }
            if (provider == null) {
                throw IllegalArgumentException("Missing binding for $workerClassName")
            }
            return provider.get()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
