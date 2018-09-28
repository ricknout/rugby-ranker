package com.ricknout.worldrugbyranker.di

import com.ricknout.worldrugbyranker.ui.rankings.RankingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeRankingsFragment(): RankingsFragment
}