package com.ricknout.rugbyranker.di

import android.app.Application
import com.ricknout.rugbyranker.RugbyRankerApplication
import com.ricknout.rugbyranker.work.RugbyRankerWorkerFactory
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, ActivityBuildersModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(rugbyRankerApplication: RugbyRankerApplication)

    fun workerFactory(): RugbyRankerWorkerFactory

    fun workerSubcomponentBuilder(): WorkerSubcomponent.Builder
}
