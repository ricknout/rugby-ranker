package dev.ricknout.rugbyranker.di

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.room.Room
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.ricknout.rugbyranker.core.api.WorldRugbyService
import dev.ricknout.rugbyranker.core.db.RankingDao
import dev.ricknout.rugbyranker.db.RugbyRankerDatabase
import dev.ricknout.rugbyranker.match.data.MatchRepository
import dev.ricknout.rugbyranker.news.data.NewsRepository
import dev.ricknout.rugbyranker.prediction.data.PredictionRepository
import dev.ricknout.rugbyranker.ranking.data.RankingDataStore
import dev.ricknout.rugbyranker.ranking.data.RankingRepository
import dev.ricknout.rugbyranker.ranking.work.RankingWorkManager
import dev.ricknout.rugbyranker.theme.data.ThemeDataStore
import dev.ricknout.rugbyranker.theme.data.ThemeRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

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
    fun provideService(retrofit: Retrofit): WorldRugbyService {
        return retrofit.create()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RugbyRankerDatabase {
        return Room.databaseBuilder(
            context,
            RugbyRankerDatabase::class.java,
            RugbyRankerDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideRankingDao(database: RugbyRankerDatabase): RankingDao {
        return database.rankingDao()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.createDataStore(
            name = DATA_STORE_NAME
        )
    }

    @Provides
    @Singleton
    fun provideRankingDataStore(dataStore: DataStore<Preferences>): RankingDataStore {
        return RankingDataStore(dataStore)
    }

    @Provides
    @Singleton
    fun provideThemeDataStore(dataStore: DataStore<Preferences>): ThemeDataStore {
        return ThemeDataStore(dataStore)
    }

    @Provides
    @Singleton
    fun provideRankingRepository(
        service: WorldRugbyService,
        dao: RankingDao,
        dataStore: RankingDataStore
    ): RankingRepository {
        return RankingRepository(service, dao, dataStore)
    }

    @Provides
    @Singleton
    fun providePredictionRepository(dao: RankingDao): PredictionRepository {
        return PredictionRepository(dao)
    }

    @Provides
    @Singleton
    fun provideMatchRepository(
        service: WorldRugbyService,
        dao: RankingDao
    ): MatchRepository {
        return MatchRepository(service, dao)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(service: WorldRugbyService): NewsRepository {
        return NewsRepository(service)
    }

    @Provides
    @Singleton
    fun provideThemeRepository(dataStore: ThemeDataStore): ThemeRepository {
        return ThemeRepository(dataStore)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideRankingWorkManager(workManager: WorkManager): RankingWorkManager {
        return RankingWorkManager(workManager)
    }

    companion object {
        private const val DATA_STORE_NAME = "rugby_ranker_data_store"
    }
}
