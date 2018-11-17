package com.ricknout.rugbyranker.ui.rankings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.ricknout.rugbyranker.R
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import androidx.core.view.isVisible
import com.ricknout.rugbyranker.ui.common.WorldRugbyRankingListAdapter
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.work.WorkInfo.State
import com.google.android.material.snackbar.Snackbar
import com.ricknout.rugbyranker.common.util.doIfVisibleToUser
import com.ricknout.rugbyranker.vo.Sport
import kotlinx.android.synthetic.main.fragment_rankings.*

class RankingsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: RankingsViewModel

    private lateinit var sport: Sport

    private lateinit var workerSnackBar: Snackbar
    private lateinit var refreshSnackBar: Snackbar

    private val worldRugbyRankingAdapter = WorldRugbyRankingListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_rankings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sportOrdinal = RankingsFragmentArgs.fromBundle(arguments).sportOrdinal
        sport = Sport.values()[sportOrdinal]
        viewModel = when (sport) {
            Sport.MENS -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                    .get(MensRankingsViewModel::class.java)
            Sport.WOMENS -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                    .get(WomensRankingsViewModel::class.java)
        }
        setupRecyclerView()
        setupSnackbars()
        setupViewModel()
        setupSwipeRefreshLayout()
    }

    private fun setupRecyclerView() {
        val dividerItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        rankingsRecyclerView.addItemDecoration(dividerItemDecoration, 0)
        rankingsRecyclerView.adapter = worldRugbyRankingAdapter
    }

    private fun setupSnackbars() {
        val coordinatorLayout = ActivityCompat.requireViewById<CoordinatorLayout>(requireActivity(), R.id.coordinatorLayout)
        workerSnackBar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_INDEFINITE)
        refreshSnackBar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_SHORT)
    }

    private fun setupViewModel() {
        viewModel.worldRugbyRankings.observe(viewLifecycleOwner, Observer { worldRugbyRankings ->
            worldRugbyRankingAdapter.submitList(worldRugbyRankings)
            val isEmpty = worldRugbyRankings?.isEmpty() ?: true
            progressBar.isVisible = isEmpty
        })
        viewModel.latestWorldRugbyRankingsWorkInfos.observe(viewLifecycleOwner, Observer { workInfos ->
            val workInfo = workInfos?.firstOrNull() ?: return@Observer
            when (workInfo.state) {
                State.RUNNING -> {
                    swipeRefreshLayout.isEnabled = false
                    doIfVisibleToUser {
                        workerSnackBar.setText(R.string.snackbar_fetching_world_rugby_rankings)
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
        viewModel.refreshingLatestWorldRugbyRankings.observe(viewLifecycleOwner, Observer { refreshingLatestWorldRugbyRankings ->
            swipeRefreshLayout.isRefreshing = refreshingLatestWorldRugbyRankings
        })
    }

    private fun setupSwipeRefreshLayout() {
        val swipeRefreshColors = resources.getIntArray(R.array.colors_swipe_refresh)
        swipeRefreshLayout.setColorSchemeColors(*swipeRefreshColors)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshLatestWorldRugbyRankings { success ->
                if (!success) {
                    doIfVisibleToUser {
                        refreshSnackBar.setText(R.string.snackbar_failed_to_refresh_world_rugby_rankings)
                        refreshSnackBar.show()
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "RankingsFragment"
        private const val ARG_SPORT_ORDINAL = "sportOrdinal"
        fun newInstance(sport: Sport): RankingsFragment {
            val rankingsFragment = RankingsFragment()
            rankingsFragment.arguments = bundleOf(ARG_SPORT_ORDINAL to sport.ordinal)
            return rankingsFragment
        }
    }
}
