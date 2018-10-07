package com.ricknout.worldrugbyranker

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ricknout.worldrugbyranker.ui.common.OnBackPressedListener
import com.ricknout.worldrugbyranker.ui.common.OnBackPressedProvider
import com.ricknout.worldrugbyranker.ui.rankings.RankingsViewModel
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.saket.fluidresize.sample.FluidContentResizer
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(), OnBackPressedProvider {

    private var onBackPressedListener: OnBackPressedListener? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: RankingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(RankingsViewModel::class.java)
        val navController = findNavController(R.id.navHostFragment)
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnNavigationItemReselectedListener {
            // Do nothing to prevent recreating of Fragments on reselect
            // TODO: Implement ViewModel event to scroll to top?
        }
        navController.addOnNavigatedListener { _, destination ->
            when (destination.id) {
                R.id.mensRankingsFragment-> {
                    bottomNavigationView.post {
                        viewModel.endEditWomensMatchResult()
                        viewModel.resetWomensAddOrEditMatchInputValid()
                    }
                }
                R.id.womensRankingsFragment-> {
                    bottomNavigationView.post {
                        viewModel.endEditMensMatchResult()
                        viewModel.resetMensAddOrEditMatchInputValid()
                    }
                }
                R.id.infoFragment-> {
                    bottomNavigationView.post {
                        viewModel.endEditMensMatchResult()
                        viewModel.resetMensAddOrEditMatchInputValid()
                        viewModel.endEditWomensMatchResult()
                        viewModel.resetWomensAddOrEditMatchInputValid()
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
