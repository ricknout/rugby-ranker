package com.ricknout.worldrugbyranker.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import androidx.room.Room
import com.ricknout.worldrugbyranker.api.WorldRugbyRankingsService
import com.ricknout.worldrugbyranker.db.WorldRugbyRankerDb
import com.ricknout.worldrugbyranker.db.WorldRugbyRankingDao
import com.ricknout.worldrugbyranker.repository.WorldRugbyRankerRepository
import com.ricknout.worldrugbyranker.work.WorldRugbyRankerWorkManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    fun provideWorldRugbyRankerWorkManager(): WorldRugbyRankerWorkManager {
        return WorldRugbyRankerWorkManager()
    }

    @Provides
    @Singleton
    fun provideWorldRugbyRankerRepository(
            worldRugbyRankingDao: WorldRugbyRankingDao,
            worldRugbyRankerWorkManager: WorldRugbyRankerWorkManager
    ) : WorldRugbyRankerRepository {
        return WorldRugbyRankerRepository(worldRugbyRankerWorkManager, worldRugbyRankingDao)
    }

    companion object {
        private const val BASE_URL = "https://cmsapi.pulselive.com/"
        private const val DATABASE_NAME = "world_rugby_ranker.db"
    }
}
