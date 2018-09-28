package com.ricknout.worldrugbyranker.ui.ranking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.ricknout.worldrugbyranker.R
import com.ricknout.worldrugbyranker.ui.common.WorldRugbyRankingListAdapter
import com.ricknout.worldrugbyranker.vo.MatchResult
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_rankings.*
import javax.inject.Inject

class RankingsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: RankingsViewModel

    private val adapter = WorldRugbyRankingListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_rankings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)
                .get(RankingsViewModel::class.java)
        viewModel.mensWorldRugbyRankings.observe(this, Observer { mensWorldRugbyRankings ->
            adapter.submitList(mensWorldRugbyRankings)
        })
        viewModel.isCalculatingMens.observe(this, Observer { isCalculatingMens ->
            resetButton.isEnabled = isCalculatingMens
        })
        // Testing calculate
        calculateButton.setOnClickListener {
            val matchResult = MatchResult(
                    homeTeamId = 37,
                    homeTeamAbbreviation = "NZL",
                    homeTeamScore = 10,
                    awayTeamId = 39,
                    awayTeamAbbreviation = "RSA",
                    awayTeamScore = 26,
                    noHomeAdvantage = false,
                    rugbyWorldCup = false
            )
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