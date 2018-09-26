package com.ricknout.worldrugbyranker.ui.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.ricknout.worldrugbyranker.R
import com.ricknout.worldrugbyranker.ui.common.WorldRugbyRankingListAdapter
import com.ricknout.worldrugbyranker.vo.MatchResult
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_rankings.*
import javax.inject.Inject

class RankingsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: RankingsViewModel

    private val adapter = WorldRugbyRankingListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_rankings, container, false)
        AndroidSupportInjection.inject(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)
                .get(RankingsViewModel::class.java)
        viewModel.mensWorldRugbyRankings.observe(this, Observer { mensWorldRugbyRankings ->
            adapter.submitList(mensWorldRugbyRankings)
        })
        viewModel.isCalculating.observe(this, Observer { isCalculating ->
            calculateButton.isEnabled = !isCalculating
            resetButton.isEnabled = isCalculating
        })
        // Testing calculate
        calculateButton.setOnClickListener {
            val matchResult = MatchResult(37, 39, 20, 10, false, false)
            viewModel.calculateMens(matchResult)
        }
        resetButton.setOnClickListener {
            viewModel.resetMens()
        }
    }

    companion object {
        const val TAG = "RankingsFragment"
        fun newInstance() = RankingsFragment()
    }
}