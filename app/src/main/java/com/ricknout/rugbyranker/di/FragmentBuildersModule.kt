package com.ricknout.rugbyranker.di

import com.ricknout.rugbyranker.ui.matches.MatchesFragment
import com.ricknout.rugbyranker.ui.rankings.RankingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeRankingsFragment(): RankingsFragment

    @ContributesAndroidInjector
    abstract fun contributeMatchesFragment(): MatchesFragment
}