package dev.ricknout.rugbyranker

import android.app.Application
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import dev.ricknout.rugbyranker.theme.data.ThemeRepository
import kotlinx.coroutines.GlobalScope
import javax.inject.Inject

@HiltAndroidApp
class RugbyRankerApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var themeRepository: ThemeRepository

    override fun onCreate() {
        super.onCreate()
        setupTheme()
        setupEmojiCompat()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun setupTheme() {
        themeRepository.setTheme(GlobalScope)
    }

    private fun setupEmojiCompat() {
        val request = FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            "Noto Color Emoji Compat",
            R.array.com_google_android_gms_fonts_certs
        )
        val config = FontRequestEmojiCompatConfig(this, request).apply { setReplaceAll(true) }
        EmojiCompat.init(config)
    }
}
