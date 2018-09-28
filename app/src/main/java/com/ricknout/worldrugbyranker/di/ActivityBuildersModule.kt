package com.ricknout.worldrugbyranker.di

import com.ricknout.worldrugbyranker.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(modules = [(FragmentBuildersModule::class)])
    abstract fun contributeMainActivity(): MainActivity

}