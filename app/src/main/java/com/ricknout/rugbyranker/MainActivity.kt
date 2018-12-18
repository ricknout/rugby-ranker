package com.ricknout.rugbyranker

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ricknout.rugbyranker.info.ui.InfoViewModel
import com.ricknout.rugbyranker.matches.ui.MensCompleteMatchesViewModel
import com.ricknout.rugbyranker.matches.ui.MensUnplayedMatchesViewModel
import com.ricknout.rugbyranker.matches.ui.WomensCompleteMatchesViewModel
import com.ricknout.rugbyranker.matches.ui.WomensUnplayedMatchesViewModel
import com.ricknout.rugbyranker.rankings.ui.MensRankingsViewModel
import com.ricknout.rugbyranker.rankings.ui.WomensRankingsViewModel
import com.ricknout.rugbyranker.ui.MensViewModel
import com.ricknout.rugbyranker.ui.WomensViewModel
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.saket.fluidresize.sample.FluidContentResizer
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mensViewModel: MensViewModel
    private lateinit var womensViewModel: WomensViewModel
    private lateinit var infoViewModel: InfoViewModel
    private lateinit var mensRankingsViewModel: MensRankingsViewModel
    private lateinit var womensRankingsViewModel: WomensRankingsViewModel
    private lateinit var mensUnplayedMatchesViewModel: MensUnplayedMatchesViewModel
    private lateinit var womensUnplayedMatchesViewModel: WomensUnplayedMatchesViewModel
    private lateinit var mensCompleteMatchesViewModel: MensCompleteMatchesViewModel
    private lateinit var womensCompleteMatchesViewModel: WomensCompleteMatchesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mensViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MensViewModel::class.java)
        womensViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(WomensViewModel::class.java)
        infoViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(InfoViewModel::class.java)
        mensRankingsViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MensRankingsViewModel::class.java)
        womensRankingsViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(WomensRankingsViewModel::class.java)
        mensUnplayedMatchesViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MensUnplayedMatchesViewModel::class.java)
        womensUnplayedMatchesViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(WomensUnplayedMatchesViewModel::class.java)
        mensCompleteMatchesViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MensCompleteMatchesViewModel::class.java)
        womensCompleteMatchesViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(WomensCompleteMatchesViewModel::class.java)
        setupBottomNavigation()
        FluidContentResizer.listen(this)
    }

    private fun setupBottomNavigation() {
        val navController = findNavController(R.id.navHostFragment)
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnNavigationItemReselectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mensFragment -> {
                    mensViewModel.reselect()
                    mensRankingsViewModel.reselect()
                    mensUnplayedMatchesViewModel.reselect()
                    mensCompleteMatchesViewModel.reselect()
                }
                R.id.womensFragment -> {
                    womensViewModel.reselect()
                    womensRankingsViewModel.reselect()
                    womensUnplayedMatchesViewModel.reselect()
                    womensCompleteMatchesViewModel.reselect()
                }
                R.id.infoFragment -> infoViewModel.reselect()
            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.mensFragment -> {
                    bottomNavigationView.post {
                        womensRankingsViewModel.endEditMatchPrediction()
                        womensRankingsViewModel.resetMatchPredictionInputState()
                    }
                }
                R.id.womensFragment -> {
                    bottomNavigationView.post {
                        mensRankingsViewModel.endEditMatchPrediction()
                        mensRankingsViewModel.resetMatchPredictionInputState()
                    }
                }
                R.id.infoFragment -> {
                    bottomNavigationView.post {
                        mensRankingsViewModel.endEditMatchPrediction()
                        mensRankingsViewModel.resetMatchPredictionInputState()
                        womensRankingsViewModel.endEditMatchPrediction()
                        womensRankingsViewModel.resetMatchPredictionInputState()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (FluidContentResizer.isAnimating()) return
        super.onBackPressed()
    }
}
