package com.ricknout.worldrugbyranker.di

import android.app.Application
import com.ricknout.worldrugbyranker.WorldRugbyRankerApplication
import com.ricknout.worldrugbyranker.work.WorldRugbyRankingsWorker
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AndroidInjectionModule::class,
            AppModule::class,
            ActivityBuildersModule::class]
)
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(worldRugbyRankerApplication: WorldRugbyRankerApplication)

    fun inject(worldRugbyRankingsWorker: WorldRugbyRankingsWorker)
}
