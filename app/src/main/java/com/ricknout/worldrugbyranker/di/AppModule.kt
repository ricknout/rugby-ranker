package com.ricknout.worldrugbyranker.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import androidx.room.Room
import com.ricknout.worldrugbyranker.AppExecutors
import com.ricknout.worldrugbyranker.MainThreadExecutor
import com.ricknout.worldrugbyranker.api.WorldRugbyRankingsService
import com.ricknout.worldrugbyranker.db.WorldRugbyRankerDb
import com.ricknout.worldrugbyranker.db.WorldRugbyRankingDao
import com.ricknout.worldrugbyranker.prefs.WorldRugbyRankerSharedPreferences
import com.ricknout.worldrugbyranker.repository.WorldRugbyRankerRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors

@Module(includes = [ViewModelModule::class])
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
                .baseUrl(BASE_URL)
                .build()
    }

    @Provides
    @Singleton
    fun provideWorldRugbyRankingsService(retrofit: Retrofit): WorldRugbyRankingsService {
        return retrofit.create(WorldRugbyRankingsService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Context): WorldRugbyRankerDb {
        return Room.databaseBuilder(context, WorldRugbyRankerDb::class.java, DATABASE_NAME)
                .build()
    }

    @Provides
    @Singleton
    fun provideWorldRugbyRankingDao(database: WorldRugbyRankerDb): WorldRugbyRankingDao {
        return database.worldRugbyRankingDao()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideWorldRugbyRankerSharedPreferences(sharedPreferences: SharedPreferences): WorldRugbyRankerSharedPreferences {
        return WorldRugbyRankerSharedPreferences(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideAppExecutors(): AppExecutors {
        return AppExecutors(
                Executors.newSingleThreadExecutor(),
                Executors.newFixedThreadPool(3),
                MainThreadExecutor()
        )
    }

    @Provides
    @Singleton
    fun provideWorldRugbyRankerRepository(
            worldRugbyRankingsService: WorldRugbyRankingsService,
            worldRugbyRankingDao: WorldRugbyRankingDao,
            worldRugbyRankerSharedPreferences: WorldRugbyRankerSharedPreferences,
            appExecutors: AppExecutors
    ) : WorldRugbyRankerRepository {
        return WorldRugbyRankerRepository(
                worldRugbyRankingsService,
                worldRugbyRankingDao,
                worldRugbyRankerSharedPreferences,
                appExecutors
        )
    }

    companion object {
        private const val BASE_URL = "https://cmsapi.pulselive.com/"
        private const val DATABASE_NAME = "world_rugby_ranker.db"
        private const val SHARED_PREFERENCES_NAME = "world_rugby_ranker_shared_preferences"
    }
}