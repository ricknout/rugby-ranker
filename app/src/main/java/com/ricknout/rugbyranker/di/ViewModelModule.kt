package com.ricknout.rugbyranker.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ricknout.rugbyranker.ui.rankings.MensRankingsViewModel
import com.ricknout.rugbyranker.ui.rankings.WomensRankingsViewModel
import com.ricknout.rugbyranker.viewmodel.RugbyRankerViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: RugbyRankerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MensRankingsViewModel::class)
    abstract fun bindMensRankingsViewModel(mensRankingsViewModel: MensRankingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WomensRankingsViewModel::class)
    abstract fun bindWomensRankingsViewModel(womensRankingsViewModel: WomensRankingsViewModel): ViewModel
}
