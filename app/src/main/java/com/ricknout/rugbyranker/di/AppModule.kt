package com.ricknout.rugbyranker.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import androidx.room.Room
import com.ricknout.rugbyranker.api.WorldRugbyRankingsService
import com.ricknout.rugbyranker.db.RugbyRankerDb
import com.ricknout.rugbyranker.db.WorldRugbyRankingDao
import com.ricknout.rugbyranker.repository.RugbyRankerRepository
import com.ricknout.rugbyranker.work.RugbyRankerWorkManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
                .baseUrl(WorldRugbyRankingsService.BASE_URL)
                .build()
    }

    @Provides
    @Singleton
    fun provideWorldRugbyRankingsService(retrofit: Retrofit): WorldRugbyRankingsService {
        return retrofit.create(WorldRugbyRankingsService::class.java)
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
    fun provideRugbyRankerWorkManager(): RugbyRankerWorkManager {
        return RugbyRankerWorkManager()
    }

    @Provides
    @Singleton
    fun provideRugbyRankerRepository(
            worldRugbyRankingDao: WorldRugbyRankingDao,
            rugbyRankerWorkManager: RugbyRankerWorkManager
    ) : RugbyRankerRepository {
        return RugbyRankerRepository(rugbyRankerWorkManager, worldRugbyRankingDao)
    }
}
