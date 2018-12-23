package com.ricknout.rugbyranker.di

import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Provider

@Subcomponent(modules = [WorkerModule::class])
interface WorkerSubcomponent {

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun workerParameters(param: WorkerParameters): Builder

        fun build(): WorkerSubcomponent
    }

    fun workers(): Map<Class<out ListenableWorker>, Provider<ListenableWorker>>
}
