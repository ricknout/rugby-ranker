package com.ricknout.worldrugbyranker.ui.rankings

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

    private var type: Int = TYPE_NONE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_rankings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)
                .get(RankingsViewModel::class.java)
        type = RankingsFragmentArgs.fromBundle(arguments).type
        when (type) {
            TYPE_MENS -> {
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
            TYPE_WOMENS -> {
                viewModel.womensWorldRugbyRankings.observe(this, Observer { womensWorldRugbyRankings ->
                    adapter.submitList(womensWorldRugbyRankings)
                })
                viewModel.isCalculatingWomens.observe(this, Observer { isCalculatingWomens ->
                    resetButton.isEnabled = isCalculatingWomens
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
                    viewModel.calculateWomens(matchResult)
                }
                resetButton.setOnClickListener {
                    viewModel.resetWomens()
                }
            }
        }
    }

    companion object {
        const val TAG = "RankingsFragment"
        private const val TYPE_NONE = -1
        private const val TYPE_MENS = 0
        private const val TYPE_WOMENS = 1
    }
}