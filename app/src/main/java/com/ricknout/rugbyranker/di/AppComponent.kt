package com.ricknout.rugbyranker.di

import com.ricknout.rugbyranker.RugbyRankerApplication
import com.ricknout.rugbyranker.work.RugbyRankerWorkerFactory
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, ActivityBuildersModule::class])
interface AppComponent : AndroidInjector<RugbyRankerApplication> {

    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<RugbyRankerApplication>

    fun workerFactory(): RugbyRankerWorkerFactory

    fun workerSubcomponentBuilder(): WorkerSubcomponent.Builder
}
