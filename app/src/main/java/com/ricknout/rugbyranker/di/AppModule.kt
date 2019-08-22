package com.ricknout.rugbyranker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.work.WorkManager
import com.ricknout.rugbyranker.RugbyRankerApplication
import com.ricknout.rugbyranker.core.api.WorldRugbyService
import com.ricknout.rugbyranker.db.RugbyRankerDb
import com.ricknout.rugbyranker.db.RugbyRankerMigrations
import com.ricknout.rugbyranker.matches.db.WorldRugbyMatchDao
import com.ricknout.rugbyranker.matches.repository.MatchesRepository
import com.ricknout.rugbyranker.matches.work.MatchesWorkManager
import com.ricknout.rugbyranker.news.db.WorldRugbyNewsDao
import com.ricknout.rugbyranker.news.repository.NewsRepository
import com.ricknout.rugbyranker.news.work.NewsWorkManager
import com.ricknout.rugbyranker.rankings.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.rankings.prefs.RankingsSharedPreferences
import com.ricknout.rugbyranker.rankings.repository.RankingsRepository
import com.ricknout.rugbyranker.rankings.work.RankingsWorkManager
import com.ricknout.rugbyranker.teams.db.WorldRugbyTeamDao
import com.ricknout.rugbyranker.teams.repository.TeamsRepository
import com.ricknout.rugbyranker.teams.work.TeamsWorkManager
import com.ricknout.rugbyranker.theme.prefs.ThemeSharedPreferences
import com.ricknout.rugbyranker.theme.repository.ThemeRepository
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

@Module(includes = [ViewModelModule::class, WorkerModule::class])
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: RugbyRankerApplication): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(WorldRugbyService.BASE_URL)
                .build()
    }

    @Provides
    @Singleton
    fun provideWorldRugbyService(retrofit: Retrofit): WorldRugbyService {
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Context): RugbyRankerDb {
        return Room.databaseBuilder(context, RugbyRankerDb::class.java, RugbyRankerDb.DATABASE_NAME)
                .addMigrations(
                        RugbyRankerMigrations.MIGRATION_1_2,
                        RugbyRankerMigrations.MIGRATION_2_3,
                        RugbyRankerMigrations.MIGRATION_3_4
                )
                .build()
    }

    @Provides
    @Singleton
    fun provideWorldRugbyRankingDao(database: RugbyRankerDb): WorldRugbyRankingDao {
        return database.worldRugbyRankingDao()
    }

    @Provides
    @Singleton
    fun provideWorldRugbyMatchDao(database: RugbyRankerDb): WorldRugbyMatchDao {
        return database.worldRugbyMatchDao()
    }

    @Provides
    @Singleton
    fun provideWorldRugbyTeamDao(database: RugbyRankerDb): WorldRugbyTeamDao {
        return database.worldRugbyTeamDao()
    }

    @Provides
    @Singleton
    fun provideWorldRugbyNewsDao(database: RugbyRankerDb): WorldRugbyNewsDao {
        return database.worldRugbyNewsDao()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideRankingsSharedPreferences(sharedPreferences: SharedPreferences): RankingsSharedPreferences {
        return RankingsSharedPreferences(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideThemeSharedPreferences(sharedPreferences: SharedPreferences): ThemeSharedPreferences {
        return ThemeSharedPreferences(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideRankingsRepository(
        worldRugbyService: WorldRugbyService,
        worldRugbyRankingDao: WorldRugbyRankingDao,
        rankingsSharedPreferences: RankingsSharedPreferences
    ): RankingsRepository {
        return RankingsRepository(worldRugbyService, worldRugbyRankingDao, rankingsSharedPreferences)
    }

    @Provides
    @Singleton
    fun provideMatchesRepository(
        worldRugbyService: WorldRugbyService,
        worldRugbyMatchDao: WorldRugbyMatchDao
    ): MatchesRepository {
        return MatchesRepository(worldRugbyService, worldRugbyMatchDao)
    }

    @Provides
    @Singleton
    fun provideThemeRepository(themeSharedPreferences: ThemeSharedPreferences): ThemeRepository {
        return ThemeRepository(themeSharedPreferences)
    }

    @Provides
    @Singleton
    fun provideTeamsRepository(
        worldRugbyService: WorldRugbyService,
        worldRugbyTeamDao: WorldRugbyTeamDao
    ): TeamsRepository {
        return TeamsRepository(worldRugbyService, worldRugbyTeamDao)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(
        worldRugbyService: WorldRugbyService,
        worldRugbyNewsDao: WorldRugbyNewsDao
    ): NewsRepository {
        return NewsRepository(worldRugbyService, worldRugbyNewsDao)
    }

    @Provides
    @Singleton
    fun provideWorkManager(context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideRankingsWorkManager(workManager: WorkManager): RankingsWorkManager {
        return RankingsWorkManager(workManager)
    }

    @Provides
    @Singleton
    fun provideMatchesWorkManager(workManager: WorkManager): MatchesWorkManager {
        return MatchesWorkManager(workManager)
    }

    @Provides
    @Singleton
    fun provideTeamsWorkManager(workManager: WorkManager): TeamsWorkManager {
        return TeamsWorkManager(workManager)
    }

    @Provides
    @Singleton
    fun provideNewsWorkManager(workManager: WorkManager): NewsWorkManager {
        return NewsWorkManager(workManager)
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "rugby_ranker_shared_preferences"
    }
}
