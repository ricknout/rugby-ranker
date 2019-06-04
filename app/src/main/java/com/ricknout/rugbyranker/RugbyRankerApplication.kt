package com.ricknout.rugbyranker

import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import androidx.work.Configuration
import com.ricknout.rugbyranker.di.AppComponent
import com.ricknout.rugbyranker.di.DaggerAppComponent
import com.ricknout.rugbyranker.theme.repository.ThemeRepository
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Inject

class RugbyRankerApplication : DaggerApplication(), Configuration.Provider {

    @Inject
    lateinit var themeRepository: ThemeRepository

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        initEmojiCompat()
        initTheme()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val daggerAppComponent = DaggerAppComponent.factory().create(this)
        appComponent = daggerAppComponent as AppComponent
        return daggerAppComponent
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
                .setWorkerFactory(appComponent.workerFactory())
                .build()
    }

    private fun initEmojiCompat() {
        val emojiCompatConfig = BundledEmojiCompatConfig(this).apply { setReplaceAll(true) }
        EmojiCompat.init(emojiCompatConfig)
    }

    private fun initTheme() {
        themeRepository.setDefaultTheme()
    }
}
