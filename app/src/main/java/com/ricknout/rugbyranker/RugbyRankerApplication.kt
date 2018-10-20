package com.ricknout.rugbyranker

import android.app.Activity
import android.app.Application
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.ricknout.rugbyranker.di.AppComponent
import com.ricknout.rugbyranker.di.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class RugbyRankerApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    lateinit var appComponent: AppComponent

    @Inject
    lateinit var workerFactory: WorkerFactory

    override fun onCreate() {
        super.onCreate()
        initDagger()
        initWorkManager()
        initEmojiCompat()
    }

    private fun initDagger() {
        appComponent = DaggerAppComponent.builder().application(this).build()
        appComponent.inject(this)
    }

    private fun initWorkManager() {
        val configuration = Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        WorkManager.initialize(applicationContext, configuration)
    }

    private fun initEmojiCompat() {
        val emojiCompatConfig = BundledEmojiCompatConfig(this).apply { setReplaceAll(true) }
        EmojiCompat.init(emojiCompatConfig)
    }

    override fun activityInjector() = dispatchingAndroidInjector
}
