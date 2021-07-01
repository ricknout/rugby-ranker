package dev.ricknout.rugbyranker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import dev.ricknout.rugbyranker.theme.data.ThemeRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import javax.inject.Inject

@HiltAndroidApp
class RugbyRankerApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var themeRepository: ThemeRepository

    @DelicateCoroutinesApi
    override fun onCreate() {
        super.onCreate()
        setupTheme()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    @DelicateCoroutinesApi
    private fun setupTheme() {
        themeRepository.setTheme(GlobalScope)
    }
}
