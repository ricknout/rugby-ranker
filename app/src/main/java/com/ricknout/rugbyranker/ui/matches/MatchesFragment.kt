package com.ricknout.rugbyranker.ui.matches

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.work.WorkInfo.State
import com.google.android.material.snackbar.Snackbar
import com.ricknout.rugbyranker.R
import com.ricknout.rugbyranker.common.util.doIfVisibleToUser
import com.ricknout.rugbyranker.ui.common.WorldRugbyMatchDateItemDecoration
import com.ricknout.rugbyranker.ui.common.WorldRugbyMatchPagedListAdapter
import com.ricknout.rugbyranker.vo.MatchStatus
import com.ricknout.rugbyranker.common.vo.Sport
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
        worldRugbyMatchPagedListAdapter = WorldRugbyMatchPagedListAdapter({
            val latestWorldRugbyMatches = viewModel.latestWorldRugbyMatches.value ?: return@WorldRugbyMatchPagedListAdapter
            worldRugbyMatchDateItemDecoration.matches = latestWorldRugbyMatches
        }, { worldRugbyMatch ->
            viewModel.predict(worldRugbyMatch)
        })
        matchesRecyclerView.adapter = worldRugbyMatchPagedListAdapter
    }

    private fun setupSnackbars() {
        val coordinatorLayout = ActivityCompat.requireViewById<CoordinatorLayout>(requireActivity(), R.id.coordinatorLayout)
        workerSnackBar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE)
        refreshSnackBar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_SHORT)
    }

    private fun setupViewModel() {
        viewModel.latestWorldRugbyMatches.observe(viewLifecycleOwner, Observer { latestWorldRugbyMatches ->
            worldRugbyMatchPagedListAdapter.submitList(latestWorldRugbyMatches)
            worldRugbyMatchDateItemDecoration.matches = latestWorldRugbyMatches
            val isEmpty = latestWorldRugbyMatches?.isEmpty() ?: true
            progressBar.isVisible = isEmpty
        })
        viewModel.latestWorldRugbyMatchesWorkInfos.observe(viewLifecycleOwner, Observer { workInfos ->
            val workInfo = workInfos?.firstOrNull() ?: return@Observer
            when (workInfo.state) {
                State.RUNNING -> {
                    swipeRefreshLayout.isEnabled = false
                    doIfVisibleToUser {
                        workerSnackBar.setText(R.string.snackbar_fetching_world_rugby_matches)
                        workerSnackBar.show()
                    }
                }
                else -> {
                    swipeRefreshLayout.isEnabled = true
                    doIfVisibleToUser {
                        root.post { workerSnackBar.dismiss() }
                    }
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
                    doIfVisibleToUser {
                        refreshSnackBar.setText(R.string.snackbar_failed_to_refresh_world_rugby_matches)
                        refreshSnackBar.show()
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "MatchesFragment"
        private const val ARG_SPORT_ORDINAL = "sportOrdinal"
        private const val ARG_MATCH_STATUS_ORDINAL = "matchStatusOrdinal"
        fun newInstance(sport: Sport, matchStatus: MatchStatus): MatchesFragment {
            val matchesFragment = MatchesFragment()
            matchesFragment.arguments = bundleOf(ARG_SPORT_ORDINAL to sport.ordinal, ARG_MATCH_STATUS_ORDINAL to matchStatus.ordinal)
            return matchesFragment
        }
    }
}
