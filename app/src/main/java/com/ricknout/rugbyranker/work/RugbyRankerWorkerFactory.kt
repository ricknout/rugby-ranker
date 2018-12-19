package com.ricknout.rugbyranker.work

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.matches.repository.MatchesRepository
import com.ricknout.rugbyranker.matches.work.MensCompleteWorldRugbyMatchesWorker
import com.ricknout.rugbyranker.matches.work.MensUnplayedWorldRugbyMatchesWorker
import com.ricknout.rugbyranker.matches.work.WomensCompleteWorldRugbyMatchesWorker
import com.ricknout.rugbyranker.matches.work.WomensUnplayedWorldRugbyMatchesWorker
import com.ricknout.rugbyranker.rankings.repository.RankingsRepository
import com.ricknout.rugbyranker.rankings.work.MensWorldRugbyRankingsWorker
import com.ricknout.rugbyranker.rankings.work.WomensWorldRugbyRankingsWorker
import javax.inject.Inject

class RugbyRankerWorkerFactory @Inject constructor(
    private val rankingsRepository: RankingsRepository,
    private val matchesRepository: MatchesRepository
) : WorkerFactory() {

    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {

        return when (workerClassName) {
            MensWorldRugbyRankingsWorker::class.java.name -> MensWorldRugbyRankingsWorker(
                    appContext, workerParameters, rankingsRepository
            )
            WomensWorldRugbyRankingsWorker::class.java.name -> WomensWorldRugbyRankingsWorker(
                    appContext, workerParameters, rankingsRepository
            )
            MensUnplayedWorldRugbyMatchesWorker::class.java.name -> MensUnplayedWorldRugbyMatchesWorker(
                    appContext, workerParameters, matchesRepository
            )
            MensCompleteWorldRugbyMatchesWorker::class.java.name -> MensCompleteWorldRugbyMatchesWorker(
                    appContext, workerParameters, matchesRepository
            )
            WomensUnplayedWorldRugbyMatchesWorker::class.java.name -> WomensUnplayedWorldRugbyMatchesWorker(
                    appContext, workerParameters, matchesRepository
            )
            WomensCompleteWorldRugbyMatchesWorker::class.java.name -> WomensCompleteWorldRugbyMatchesWorker(
                    appContext, workerParameters, matchesRepository
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
