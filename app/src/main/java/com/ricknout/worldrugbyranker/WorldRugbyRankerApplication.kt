package com.ricknout.worldrugbyranker

import android.app.Activity
import android.app.Application
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import com.ricknout.worldrugbyranker.di.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class WorldRugbyRankerApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        initDagger()
        initEmojiCompat()
    }

    private fun initDagger() {
        DaggerAppComponent.builder().application(this).build().inject(this)
    }

    private fun initEmojiCompat() {
        val emojiCompatConfig = BundledEmojiCompatConfig(this).apply { setReplaceAll(true) }
        EmojiCompat.init(emojiCompatConfig)
    }

    override fun activityInjector() = dispatchingAndroidInjector
}