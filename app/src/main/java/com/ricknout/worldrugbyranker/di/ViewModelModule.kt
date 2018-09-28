package com.ricknout.worldrugbyranker.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ricknout.worldrugbyranker.ui.rankings.RankingsViewModel
import com.ricknout.worldrugbyranker.viewmodel.WorldRugbyRankerViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: WorldRugbyRankerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(RankingsViewModel::class)
    abstract fun bindRankingsViewModel(rankingsViewModel: RankingsViewModel): ViewModel
}
