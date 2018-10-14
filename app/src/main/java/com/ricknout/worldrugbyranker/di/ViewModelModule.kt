package com.ricknout.worldrugbyranker.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ricknout.worldrugbyranker.ui.rankings.MensRankingsViewModel
import com.ricknout.worldrugbyranker.ui.rankings.WomensRankingsViewModel
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
    @ViewModelKey(MensRankingsViewModel::class)
    abstract fun bindMensRankingsViewModel(mensRankingsViewModel: MensRankingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WomensRankingsViewModel::class)
    abstract fun bindWomensRankingsViewModel(womensRankingsViewModel: WomensRankingsViewModel): ViewModel
}
