package com.ricknout.rugbyranker

import android.app.Activity
import android.app.Application
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import com.ricknout.rugbyranker.di.AppComponent
import com.ricknout.rugbyranker.di.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class RugbyRankerApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        initDagger()
        initEmojiCompat()
    }

    private fun initDagger() {
        appComponent = DaggerAppComponent.builder().application(this).build()
        appComponent.inject(this)
    }

    private fun initEmojiCompat() {
        val emojiCompatConfig = BundledEmojiCompatConfig(this).apply { setReplaceAll(true) }
        EmojiCompat.init(emojiCompatConfig)
    }

    override fun activityInjector() = dispatchingAndroidInjector
}
