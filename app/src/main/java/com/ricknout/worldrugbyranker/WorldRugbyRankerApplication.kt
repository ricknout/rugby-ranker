package com.ricknout.worldrugbyranker

import android.app.Activity
import android.app.Application
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import com.ricknout.worldrugbyranker.di.AppComponent
import com.ricknout.worldrugbyranker.di.DaggerAppComponent
import com.ricknout.worldrugbyranker.work.MensWorldRugbyRankingsWorker
import com.ricknout.worldrugbyranker.work.WomensWorldRugbyRankingsWorker
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorldRugbyRankerApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        initDagger()
        initEmojiCompat()
        initWorkers()
    }

    private fun initDagger() {
        appComponent = DaggerAppComponent.builder().application(this).build()
        appComponent.inject(this)
    }

    private fun initEmojiCompat() {
        val emojiCompatConfig = BundledEmojiCompatConfig(this).apply { setReplaceAll(true) }
        EmojiCompat.init(emojiCompatConfig)
    }

    private fun initWorkers() {
        val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        val mensWorkRequest = PeriodicWorkRequest.Builder(
                MensWorldRugbyRankingsWorker::class.java, WORK_REQUEST_REPEAT_INTERVAL, WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT
        ).setConstraints(constraints).build()
        val womensWorkRequest = PeriodicWorkRequest.Builder(
                WomensWorldRugbyRankingsWorker::class.java, WORK_REQUEST_REPEAT_INTERVAL, WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT
        ).setConstraints(constraints).build()
        val workManager = WorkManager.getInstance()
        workManager.enqueueUniquePeriodicWork(MensWorldRugbyRankingsWorker.UNIQUE_WORK_NAME, WORK_REQUEST_EXISTING_PERIODIC_WORK_POLICY, mensWorkRequest)
        workManager.enqueueUniquePeriodicWork(WomensWorldRugbyRankingsWorker.UNIQUE_WORK_NAME, WORK_REQUEST_EXISTING_PERIODIC_WORK_POLICY, womensWorkRequest)
    }

    override fun activityInjector() = dispatchingAndroidInjector

    companion object {
        private const val WORK_REQUEST_REPEAT_INTERVAL = 1L
        private val WORK_REQUEST_REPEAT_INTERVAL_TIME_UNIT = TimeUnit.DAYS
        private val WORK_REQUEST_EXISTING_PERIODIC_WORK_POLICY = ExistingPeriodicWorkPolicy.KEEP
    }
}
