package com.ricknout.worldrugbyranker

import android.os.Bundle
import com.ricknout.worldrugbyranker.ui.ranking.RankingsFragment
import com.ricknout.worldrugbyranker.util.replaceFragment
import dagger.android.support.DaggerAppCompatActivity

class MainActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            replaceFragment(R.id.fragmentContainer, RankingsFragment.newInstance(), RankingsFragment.TAG)
        }
    }
}
