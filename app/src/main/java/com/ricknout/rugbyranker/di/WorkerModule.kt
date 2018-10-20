package com.ricknout.rugbyranker.di

import androidx.work.WorkerFactory
import com.ricknout.rugbyranker.work.RugbyRankerWorkerFactory
import dagger.Binds
import dagger.Module

@Module
abstract class WorkerModule {

    @Binds
    abstract fun bindWorkerFactory(factory: RugbyRankerWorkerFactory): WorkerFactory
}
