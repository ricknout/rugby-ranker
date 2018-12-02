package com.ricknout.rugbyranker.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import androidx.room.Room
import com.ricknout.rugbyranker.api.WorldRugbyService
import com.ricknout.rugbyranker.db.RugbyRankerDb
import com.ricknout.rugbyranker.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.prefs.RugbyRankerSharedPreferences
import com.ricknout.rugbyranker.repository.RugbyRankerRepository
import com.ricknout.rugbyranker.work.RugbyRankerWorkManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
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
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Context): RugbyRankerDb {
        return Room.databaseBuilder(context, RugbyRankerDb::class.java, RugbyRankerDb.DATABASE_NAME)
                .build()
    }

    @Provides
    @Singleton
    fun provideWorldRugbyRankingDao(database: RugbyRankerDb): WorldRugbyRankingDao {
        return database.worldRugbyRankingDao()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(RugbyRankerSharedPreferences.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideRugbyRankerSharedPreferences(sharedPreferences: SharedPreferences): RugbyRankerSharedPreferences {
        return RugbyRankerSharedPreferences(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideExecutor(): Executor {
        return Executors.newSingleThreadExecutor()
    }

    @Provides
    @Singleton
    fun provideRugbyRankerRepository(
        worldRugbyService: WorldRugbyService,
        worldRugbyRankingDao: WorldRugbyRankingDao,
        rugbyRankerSharedPreferences: RugbyRankerSharedPreferences,
        executor: Executor
    ): RugbyRankerRepository {
        return RugbyRankerRepository(worldRugbyService, worldRugbyRankingDao, rugbyRankerSharedPreferences, executor)
    }

    @Provides
    @Singleton
    fun provideRugbyRankerWorkManager(): RugbyRankerWorkManager {
        return RugbyRankerWorkManager()
    }
}
