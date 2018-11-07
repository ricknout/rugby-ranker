package com.ricknout.rugbyranker.ui.matches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.ricknout.rugbyranker.R
import com.ricknout.rugbyranker.ui.common.WorldRugbyMatchPagedListAdapter
import com.ricknout.rugbyranker.vo.MatchStatus
import com.ricknout.rugbyranker.vo.Sport
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_matches.*

class MatchesFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MatchesViewModel

    private lateinit var sport: Sport
    private lateinit var matchStatus: MatchStatus

    private val worldRugbyMatchPagedListAdapter = WorldRugbyMatchPagedListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_matches, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sportOrdinal = MatchesFragmentArgs.fromBundle(arguments).sportOrdinal
        sport = Sport.values()[sportOrdinal]
        val matchStatusOrdinal = MatchesFragmentArgs.fromBundle(arguments).matchStatusOrdinal
        matchStatus = MatchStatus.values()[matchStatusOrdinal]
        viewModel = when (sport) {
            Sport.MENS -> {
                when (matchStatus) {
                    MatchStatus.UNPLAYED -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                            .get(MensUnplayedMatchesViewModel::class.java)
                    MatchStatus.COMPLETE -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                            .get(MensCompleteMatchesViewModel::class.java)
                }
            }
            Sport.WOMENS -> {
                when (matchStatus) {
                    MatchStatus.UNPLAYED -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                            .get(WomensUnplayedMatchesViewModel::class.java)
                    MatchStatus.COMPLETE -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                            .get(WomensCompleteMatchesViewModel::class.java)
                }
            }
        }
        setupRecyclerView()
        setupViewModel()
    }

    private fun setupRecyclerView() {
        matchesRecyclerView.adapter = worldRugbyMatchPagedListAdapter
    }

    private fun setupViewModel() {
        viewModel.latestWorldRugbyMatches.observe(this, Observer { latestWorldRugbyMatches ->
            worldRugbyMatchPagedListAdapter.submitList(latestWorldRugbyMatches)
        })
    }
}
