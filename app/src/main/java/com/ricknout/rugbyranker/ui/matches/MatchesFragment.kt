package com.ricknout.rugbyranker.ui.matches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.work.WorkInfo.State
import com.google.android.material.snackbar.Snackbar
import com.ricknout.rugbyranker.R
import com.ricknout.rugbyranker.ui.common.WorldRugbyMatchDateItemDecoration
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

    private lateinit var workerSnackBar: Snackbar
    private lateinit var refreshSnackBar: Snackbar

    private lateinit var worldRugbyMatchPagedListAdapter: WorldRugbyMatchPagedListAdapter

    private lateinit var worldRugbyMatchDateItemDecoration: WorldRugbyMatchDateItemDecoration

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
        setupSnackbars()
        setupViewModel()
        setupSwipeRefreshLayout()
    }

    private fun setupRecyclerView() {
        worldRugbyMatchDateItemDecoration = WorldRugbyMatchDateItemDecoration(requireContext())
        matchesRecyclerView.addItemDecoration(worldRugbyMatchDateItemDecoration, 0)
        worldRugbyMatchPagedListAdapter = WorldRugbyMatchPagedListAdapter {
            val latestWorldRugbyMatches = viewModel.latestWorldRugbyMatches.value ?: return@WorldRugbyMatchPagedListAdapter
            worldRugbyMatchDateItemDecoration.matches = latestWorldRugbyMatches
        }
        matchesRecyclerView.adapter = worldRugbyMatchPagedListAdapter
    }

    private fun setupSnackbars() {
        workerSnackBar = Snackbar.make(root, "", Snackbar.LENGTH_INDEFINITE)
        refreshSnackBar = Snackbar.make(root, "", Snackbar.LENGTH_SHORT)
    }

    private fun setupViewModel() {
        viewModel.latestWorldRugbyMatches.observe(viewLifecycleOwner, Observer { latestWorldRugbyMatches ->
            worldRugbyMatchPagedListAdapter.submitList(latestWorldRugbyMatches)
            matchesRecyclerView.invalidateItemDecorations()
            val isEmpty = latestWorldRugbyMatches?.isEmpty() ?: true
            progressBar.isVisible = isEmpty
        })
        viewModel.latestWorldRugbyMatchesWorkInfos.observe(viewLifecycleOwner, Observer { workInfos ->
            val workInfo = workInfos?.firstOrNull() ?: return@Observer
            when (workInfo.state) {
                State.RUNNING -> {
                    swipeRefreshLayout.isEnabled = false
                    workerSnackBar.setText(R.string.snackbar_fetching_world_rugby_matches)
                    workerSnackBar.show()
                }
                else -> {
                    swipeRefreshLayout.isEnabled = true
                    root.post { workerSnackBar.dismiss() }
                }
            }
        })
        viewModel.refreshingLatestWorldRugbyMatches.observe(viewLifecycleOwner, Observer { refreshingLatestWorldRugbyMatches ->
            swipeRefreshLayout.isRefreshing = refreshingLatestWorldRugbyMatches
        })
    }

    private fun setupSwipeRefreshLayout() {
        val swipeRefreshColors = resources.getIntArray(R.array.colors_swipe_refresh)
        swipeRefreshLayout.setColorSchemeColors(*swipeRefreshColors)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshLatestWorldRugbyMatches { success ->
                if (!success) {
                    refreshSnackBar.setText(R.string.snackbar_failed_to_refresh_world_rugby_matches)
                    refreshSnackBar.show()
                }
            }
        }
    }
}
