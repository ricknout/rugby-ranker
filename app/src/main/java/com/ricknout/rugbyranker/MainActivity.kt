package com.ricknout.rugbyranker

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
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
import com.ricknout.rugbyranker.rankings.ui.MensRankingsViewModel
import com.ricknout.rugbyranker.rankings.ui.WomensRankingsViewModel
import com.ricknout.rugbyranker.ui.MensViewModel
import com.ricknout.rugbyranker.ui.WomensViewModel
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : DaggerAppCompatActivity(R.layout.activity_main) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val mensViewModel: MensViewModel by viewModels { viewModelFactory }
    private val womensViewModel: WomensViewModel by viewModels { viewModelFactory }
    private val newsViewModel: NewsViewModel by viewModels { viewModelFactory }
    private val textArticlesViewModel: TextArticlesViewModel by viewModels { viewModelFactory }
    private val videoArticlesViewModel: VideoArticlesViewModel by viewModels { viewModelFactory }
    private val infoViewModel: InfoViewModel by viewModels { viewModelFactory }
    private val mensRankingsViewModel: MensRankingsViewModel by viewModels { viewModelFactory }
    private val womensRankingsViewModel: WomensRankingsViewModel by viewModels { viewModelFactory }
    private val mensLiveMatchesViewModel: MensLiveMatchesViewModel by viewModels { viewModelFactory }
    private val womensLiveMatchesViewModel: WomensLiveMatchesViewModel by viewModels { viewModelFactory }
    private val mensUnplayedMatchesViewModel: MensUnplayedMatchesViewModel by viewModels { viewModelFactory }
    private val womensUnplayedMatchesViewModel: WomensUnplayedMatchesViewModel by viewModels { viewModelFactory }
    private val mensCompleteMatchesViewModel: MensCompleteMatchesViewModel by viewModels { viewModelFactory }
    private val womensCompleteMatchesViewModel: WomensCompleteMatchesViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnNavigationItemReselectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mensFragment -> {
                    mensViewModel.scrollToTop()
                    mensRankingsViewModel.scrollToTop()
                    mensLiveMatchesViewModel.scrollToTop()
                    mensUnplayedMatchesViewModel.scrollToTop()
                    mensCompleteMatchesViewModel.scrollToTop()
                }
                R.id.womensFragment -> {
                    womensViewModel.scrollToTop()
                    womensRankingsViewModel.scrollToTop()
                    womensLiveMatchesViewModel.scrollToTop()
                    womensUnplayedMatchesViewModel.scrollToTop()
                    womensCompleteMatchesViewModel.scrollToTop()
                }
                R.id.newsFragment -> {
                    newsViewModel.scrollToTop()
                    textArticlesViewModel.scrollToTop()
                    videoArticlesViewModel.scrollToTop()
                }
                R.id.infoNavGraph -> infoViewModel.scrollToTop()
            }
        }
    }
}
