package com.ricknout.rugbyranker.work

import android.content.Context
import android.util.Log
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
        val workerClass: Class<out ListenableWorker>
        try {
            workerClass = Class.forName(workerClassName).asSubclass(ListenableWorker::class.java)
        } catch (e: ClassNotFoundException) {
            Log.e(TAG, "Class not found: $workerClassName")
            return null
        }
        val creator = workers[workerClass] ?: workers.entries.firstOrNull {
            workerClass.isAssignableFrom(it.key)
        }?.value ?: throw IllegalArgumentException("Unknown worker class $workerClass")
        try {
            return creator.get()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    companion object {
        private const val TAG = "RRWorkerFactory"
    }
}
