package com.ricknout.rugbyranker.work

import android.content.Context
import androidx.work.WorkerParameters
import com.ricknout.rugbyranker.RugbyRankerApplication
import com.ricknout.rugbyranker.vo.RankingsType

class WomensWorldRugbyRankingsWorker(context: Context, workerParams: WorkerParameters) : WorldRugbyRankingsWorker(context, workerParams) {

    override fun getRankingsType() = RankingsType.WOMENS

    override fun doWork(): Result {
        (applicationContext as RugbyRankerApplication).appComponent.inject(this)
        return super.doWork()
    }

    companion object {
        const val UNIQUE_WORK_NAME = "world_rugby_rankings_worker_womens"
    }
}
