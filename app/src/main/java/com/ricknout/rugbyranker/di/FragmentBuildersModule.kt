package com.ricknout.rugbyranker.di

import com.ricknout.rugbyranker.info.ui.InfoFragment
import com.ricknout.rugbyranker.ui.SportFragment
import com.ricknout.rugbyranker.matches.ui.MatchesFragment
import com.ricknout.rugbyranker.rankings.ui.RankingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSportFragment(): SportFragment

    @ContributesAndroidInjector
    abstract fun contributeInfoFragment(): InfoFragment

    @ContributesAndroidInjector
    abstract fun contributeRankingsFragment(): RankingsFragment

    @ContributesAndroidInjector
    abstract fun contributeMatchesFragment(): MatchesFragment
}
