package com.ricknout.rugbyranker

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ricknout.rugbyranker.common.ui.OnBackPressedListener
import com.ricknout.rugbyranker.common.ui.OnBackPressedProvider
import com.ricknout.rugbyranker.ui.rankings.MensRankingsViewModel
import com.ricknout.rugbyranker.ui.rankings.WomensRankingsViewModel
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.saket.fluidresize.sample.FluidContentResizer
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), OnBackPressedProvider {

    private var onBackPressedListener: OnBackPressedListener? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mensRankingsViewModel: MensRankingsViewModel
    private lateinit var womensRankingsViewModel: WomensRankingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mensRankingsViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MensRankingsViewModel::class.java)
        womensRankingsViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(WomensRankingsViewModel::class.java)
        val navController = findNavController(R.id.navHostFragment)
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnNavigationItemReselectedListener {
            // Do nothing to prevent recreating of Fragments on reselect
            // TODO: Implement ViewModel event to scroll to top?
        }
        navController.addOnNavigatedListener { _, destination ->
            when (destination.id) {
                R.id.mensRankingsFragment -> {
                    bottomNavigationView.post {
                        womensRankingsViewModel.endEditMatchResult()
                    }
                }
                R.id.womensRankingsFragment -> {
                    bottomNavigationView.post {
                        mensRankingsViewModel.endEditMatchResult()
                    }
                }
                R.id.infoFragment -> {
                    bottomNavigationView.post {
                        mensRankingsViewModel.endEditMatchResult()
                        womensRankingsViewModel.endEditMatchResult()
                    }
                }
            }
        }
        FluidContentResizer.listen(this)
    }

    override fun setOnBackPressedListener(onBackPressedListener: OnBackPressedListener?) {
        this.onBackPressedListener = onBackPressedListener
    }

    override fun onBackPressed() {
        val onBackPressedHandled = onBackPressedListener?.onBackPressed() ?: false
        if (!onBackPressedHandled) super.onBackPressed()
    }

    override fun onDestroy() {
        onBackPressedListener = null
        super.onDestroy()
    }
}
