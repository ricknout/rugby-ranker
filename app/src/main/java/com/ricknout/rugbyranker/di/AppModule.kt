package com.ricknout.rugbyranker.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import androidx.room.Room
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.ricknout.rugbyranker.core.api.WorldRugbyService
import com.ricknout.rugbyranker.db.RugbyRankerDb
import com.ricknout.rugbyranker.db.RugbyRankerMigrations
import com.ricknout.rugbyranker.matches.db.WorldRugbyMatchDao
import com.ricknout.rugbyranker.rankings.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.rankings.prefs.RankingsSharedPreferences
import com.ricknout.rugbyranker.matches.repository.MatchesRepository
import com.ricknout.rugbyranker.rankings.repository.RankingsRepository
import com.ricknout.rugbyranker.matches.work.MatchesWorkManager
import com.ricknout.rugbyranker.rankings.work.RankingsWorkManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

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
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
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
        return MatchesRepository(worldRugbyService, worldRugbyMatchDao) }

    @Provides
    @Singleton
    fun provideRankingsWorkManager(): RankingsWorkManager {
        return RankingsWorkManager()
    }

    @Provides
    @Singleton
    fun provideMatchesWorkManager(): MatchesWorkManager {
        return MatchesWorkManager()
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "rugby_ranker_shared_preferences"
    }
}
