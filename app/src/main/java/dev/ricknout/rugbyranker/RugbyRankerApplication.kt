package dev.ricknout.rugbyranker

import android.app.Application
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
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun setupTheme() {
        themeRepository.setTheme(GlobalScope)
    }
}
