package com.ricknout.worldrugbyranker.ui.rankings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.ricknout.worldrugbyranker.R
import com.ricknout.worldrugbyranker.ui.common.MatchResultListAdapter
import com.ricknout.worldrugbyranker.ui.common.WorldRugbyRankingListAdapter
import com.ricknout.worldrugbyranker.vo.MatchResult
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_rankings.*
import java.util.Random
import javax.inject.Inject

class RankingsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: RankingsViewModel

    private val rankingsAdapter = WorldRugbyRankingListAdapter()
    private val matchesAdapter = MatchResultListAdapter()

    private var type: Int = TYPE_NONE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_rankings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rankingsRecyclerView.adapter = rankingsAdapter
        matchesRecyclerView.adapter = matchesAdapter
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)
                .get(RankingsViewModel::class.java)
        type = RankingsFragmentArgs.fromBundle(arguments).type
        when (type) {
            TYPE_MENS -> {
                viewModel.mensWorldRugbyRankings.observe(this, Observer { mensWorldRugbyRankings ->
                    rankingsAdapter.submitList(mensWorldRugbyRankings)
                    val isEmpty = mensWorldRugbyRankings?.isEmpty() ?: true
                    calculateButton.isEnabled = !isEmpty
                })
                viewModel.isCalculatingMens.observe(this, Observer { isCalculatingMens ->
                    resetButton.isEnabled = isCalculatingMens
                })
                viewModel.mensMatches.observe(this, Observer { mensMatches ->
                    matchesAdapter.submitList(mensMatches)
                })
                // Testing calculate
                calculateButton.setOnClickListener {
                    val matchResult = MatchResult(
                            homeTeamId = 37,
                            homeTeamAbbreviation = "NZL",
                            homeTeamScore = Random().nextInt(100),
                            awayTeamId = 39,
                            awayTeamAbbreviation = "RSA",
                            awayTeamScore = Random().nextInt(100),
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
                    rankingsAdapter.submitList(womensWorldRugbyRankings)
                    val isEmpty = womensWorldRugbyRankings?.isEmpty() ?: true
                    calculateButton.isEnabled = !isEmpty
                })
                viewModel.isCalculatingWomens.observe(this, Observer { isCalculatingWomens ->
                    resetButton.isEnabled = isCalculatingWomens
                })
                viewModel.womensMatches.observe(this, Observer { womensMatches ->
                    matchesAdapter.submitList(womensMatches)
                })
                // Testing calculate
                calculateButton.setOnClickListener {
                    val matchResult = MatchResult(
                            homeTeamId = 2580,
                            homeTeamAbbreviation = "NZL",
                            homeTeamScore = Random().nextInt(100),
                            awayTeamId = 2582,
                            awayTeamAbbreviation = "RSA",
                            awayTeamScore = Random().nextInt(100),
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