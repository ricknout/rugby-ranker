package com.ricknout.rugbyranker.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ricknout.rugbyranker.matches.ui.MensCompleteMatchesViewModel
import com.ricknout.rugbyranker.matches.ui.MensUnplayedMatchesViewModel
import com.ricknout.rugbyranker.matches.ui.WomensCompleteMatchesViewModel
import com.ricknout.rugbyranker.matches.ui.WomensUnplayedMatchesViewModel
import com.ricknout.rugbyranker.rankings.ui.MensRankingsViewModel
import com.ricknout.rugbyranker.rankings.ui.WomensRankingsViewModel
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

    @Binds
    @IntoMap
    @ViewModelKey(MensUnplayedMatchesViewModel::class)
    abstract fun bindMensUnplayedMatchesViewModel(mensUnplayedMatchesViewModel: MensUnplayedMatchesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MensCompleteMatchesViewModel::class)
    abstract fun bindMensCompleteMatchesViewModel(mensCompleteMatchesViewModel: MensCompleteMatchesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WomensUnplayedMatchesViewModel::class)
    abstract fun bindWomensUnplayedMatchesViewModel(womensUnplayedMatchesViewModel: WomensUnplayedMatchesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WomensCompleteMatchesViewModel::class)
    abstract fun bindWomensCompleteMatchesViewModel(womensCompleteMatchesViewModel: WomensCompleteMatchesViewModel): ViewModel
}
