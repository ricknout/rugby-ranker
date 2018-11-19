package com.ricknout.rugbyranker.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import androidx.room.Room
import com.ricknout.rugbyranker.common.api.WorldRugbyService
import com.ricknout.rugbyranker.db.RugbyRankerDb
import com.ricknout.rugbyranker.db.RugbyRankerMigrations
import com.ricknout.rugbyranker.db.WorldRugbyMatchDao
import com.ricknout.rugbyranker.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.prefs.RankingsSharedPreferences
import com.ricknout.rugbyranker.repository.MatchesRepository
import com.ricknout.rugbyranker.repository.RankingsRepository
import com.ricknout.rugbyranker.work.RugbyRankerWorkManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Module(includes = [ViewModelModule::class, WorkerModule::class])
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(WorldRugbyService.BASE_URL)
                .build()
    }

    @Provides
    @Singleton
    fun provideWorldRugbyService(retrofit: Retrofit): WorldRugbyService {
        return retrofit.create(WorldRugbyService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Context): RugbyRankerDb {
        return Room.databaseBuilder(context, RugbyRankerDb::class.java, RugbyRankerDb.DATABASE_NAME)
                .addMigrations(RugbyRankerMigrations.MIGRATION_1_2)
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
    fun provideExecutor(): Executor {
        return Executors.newSingleThreadExecutor()
    }

    @Provides
    @Singleton
    fun provideRankingsRepository(
            worldRugbyService: WorldRugbyService,
            worldRugbyRankingDao: WorldRugbyRankingDao,
            rankingsSharedPreferences: RankingsSharedPreferences,
            executor: Executor
    ) : RankingsRepository {
        return RankingsRepository(worldRugbyService, worldRugbyRankingDao, rankingsSharedPreferences, executor)
    }

    @Provides
    @Singleton
    fun provideMatchesRepository(
            worldRugbyService: WorldRugbyService,
            worldRugbyMatchDao: WorldRugbyMatchDao,
            executor: Executor
    ) : MatchesRepository {
        return MatchesRepository(worldRugbyService, worldRugbyMatchDao, executor)
    }

    @Provides
    @Singleton
    fun provideRugbyRankerWorkManager(): RugbyRankerWorkManager {
        return RugbyRankerWorkManager()
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "rugby_ranker_shared_preferences"
    }
}
