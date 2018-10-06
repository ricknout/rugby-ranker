package com.ricknout.worldrugbyranker

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ricknout.worldrugbyranker.ui.common.OnBackPressedListener
import com.ricknout.worldrugbyranker.ui.common.OnBackPressedProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.saket.fluidresize.sample.FluidContentResizer

class MainActivity : DaggerAppCompatActivity(), OnBackPressedProvider {

    private var onBackPressedListener: OnBackPressedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = findNavController(R.id.navHostFragment)
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnNavigationItemReselectedListener {
            // Do nothing to prevent recreating of Fragments on reselect
            // TODO: Implement ViewModel event to scroll to top?
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
