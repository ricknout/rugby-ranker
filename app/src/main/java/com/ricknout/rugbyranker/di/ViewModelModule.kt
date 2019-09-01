package com.ricknout.rugbyranker.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ricknout.rugbyranker.info.ui.InfoViewModel
import com.ricknout.rugbyranker.live.ui.MensLiveMatchesViewModel
import com.ricknout.rugbyranker.live.ui.WomensLiveMatchesViewModel
import com.ricknout.rugbyranker.matches.ui.MensCompleteMatchesViewModel
import com.ricknout.rugbyranker.matches.ui.MensUnplayedMatchesViewModel
import com.ricknout.rugbyranker.matches.ui.WomensCompleteMatchesViewModel
import com.ricknout.rugbyranker.matches.ui.WomensUnplayedMatchesViewModel
import com.ricknout.rugbyranker.news.ui.NewsViewModel
import com.ricknout.rugbyranker.news.ui.TextArticlesViewModel
import com.ricknout.rugbyranker.news.ui.VideoArticlesViewModel
import com.ricknout.rugbyranker.prediction.ui.MensPredictionViewModel
import com.ricknout.rugbyranker.prediction.ui.WomensPredictionViewModel
import com.ricknout.rugbyranker.rankings.ui.MensRankingsViewModel
import com.ricknout.rugbyranker.rankings.ui.WomensRankingsViewModel
import com.ricknout.rugbyranker.teams.ui.MensTeamsViewModel
import com.ricknout.rugbyranker.teams.ui.WomensTeamsViewModel
import com.ricknout.rugbyranker.theme.ui.ThemeViewModel
import com.ricknout.rugbyranker.ui.MensViewModel
import com.ricknout.rugbyranker.ui.WomensViewModel
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
    @ViewModelKey(MensViewModel::class)
    abstract fun bindMensViewModel(mensViewModel: MensViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WomensViewModel::class)
    abstract fun bindWomensViewModel(womensViewModel: WomensViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InfoViewModel::class)
    abstract fun bindInfoViewModel(infoViewModel: InfoViewModel): ViewModel

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
    @ViewModelKey(MensLiveMatchesViewModel::class)
    abstract fun bindMensLiveMatchesViewModel(mensLiveMatchesViewModel: MensLiveMatchesViewModel): ViewModel

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
    @ViewModelKey(WomensLiveMatchesViewModel::class)
    abstract fun bindWomensLiveMatchesViewModel(womensLiveMatchesViewModel: WomensLiveMatchesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WomensUnplayedMatchesViewModel::class)
    abstract fun bindWomensUnplayedMatchesViewModel(womensUnplayedMatchesViewModel: WomensUnplayedMatchesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WomensCompleteMatchesViewModel::class)
    abstract fun bindWomensCompleteMatchesViewModel(womensCompleteMatchesViewModel: WomensCompleteMatchesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ThemeViewModel::class)
    abstract fun bindThemeViewModel(themeViewModel: ThemeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MensPredictionViewModel::class)
    abstract fun bindMensPredictionViewModel(mensPredictionViewModel: MensPredictionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WomensPredictionViewModel::class)
    abstract fun bindWomensPredictionViewModel(womensPredictionViewModel: WomensPredictionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MensTeamsViewModel::class)
    abstract fun bindMensTeamsViewModel(mensTeamsViewModel: MensTeamsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WomensTeamsViewModel::class)
    abstract fun bindWomensTeamsViewModel(womensTeamsViewModel: WomensTeamsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewsViewModel::class)
    abstract fun bindNewsViewModel(newsViewModel: NewsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TextArticlesViewModel::class)
    abstract fun bindTextArticlesViewModel(textArticlesViewModel: TextArticlesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VideoArticlesViewModel::class)
    abstract fun bindVideoArticlesViewModel(videoArticlesViewModel: VideoArticlesViewModel): ViewModel
}
