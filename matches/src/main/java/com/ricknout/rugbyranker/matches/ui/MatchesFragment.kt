package com.ricknout.rugbyranker.matches.ui

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo.State
import com.google.android.material.snackbar.Snackbar
import com.ricknout.rugbyranker.core.livedata.EventObserver
import com.ricknout.rugbyranker.core.ui.dagger.DaggerAndroidXFragment
import com.ricknout.rugbyranker.core.util.doIfResumed
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.matches.MatchesNavGraphDirections
import com.ricknout.rugbyranker.matches.R
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_matches.*

class MatchesFragment : DaggerAndroidXFragment(R.layout.fragment_matches) {

    private val args: MatchesFragmentArgs by navArgs()

    private val sport: Sport by lazy { args.sport }
    private val matchStatus: MatchStatus by lazy { args.matchStatus }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MatchesViewModel by lazy {
        when {
            sport == Sport.MENS && matchStatus == MatchStatus.UNPLAYED -> activityViewModels<MensUnplayedMatchesViewModel> { viewModelFactory }.value
            sport == Sport.MENS && matchStatus == MatchStatus.COMPLETE -> activityViewModels<MensCompleteMatchesViewModel> { viewModelFactory }.value
            sport == Sport.WOMENS && matchStatus == MatchStatus.UNPLAYED -> activityViewModels<WomensUnplayedMatchesViewModel> { viewModelFactory }.value
            sport == Sport.WOMENS && matchStatus == MatchStatus.COMPLETE -> activityViewModels<WomensCompleteMatchesViewModel> { viewModelFactory }.value
            else -> throw IllegalArgumentException("Cannot handle $sport and $matchStatus combination in MatchesFragment")
        }
    }

    private val coordinatorLayout by lazy {
        ActivityCompat.requireViewById<CoordinatorLayout>(requireActivity(), R.id.coordinatorLayout)
    }

    private var workerSnackBar: Snackbar? = null

    private lateinit var worldRugbyMatchPagedListAdapter: WorldRugbyMatchPagedListAdapter
    private lateinit var worldRugbyMatchDateItemDecoration: WorldRugbyMatchDateItemDecoration

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setupViewModel()
        setupSwipeRefreshLayout()
    }

    private fun setupRecyclerView() {
        worldRugbyMatchDateItemDecoration = WorldRugbyMatchDateItemDecoration(requireContext())
        matchesRecyclerView.addItemDecoration(worldRugbyMatchDateItemDecoration, 0)
        worldRugbyMatchPagedListAdapter = WorldRugbyMatchPagedListAdapter({
            val latestWorldRugbyMatches = viewModel.latestWorldRugbyMatches.value
                    ?: return@WorldRugbyMatchPagedListAdapter
            worldRugbyMatchDateItemDecoration.matches = latestWorldRugbyMatches
        }, { worldRugbyMatch ->
            viewModel.predict(worldRugbyMatch)
        })
        matchesRecyclerView.adapter = worldRugbyMatchPagedListAdapter
        matchesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                viewModel.onScroll(delta = dy)
            }
        })
    }

    private fun setupViewModel() {
        viewModel.latestWorldRugbyMatches.observe(viewLifecycleOwner, Observer { latestWorldRugbyMatches ->
            worldRugbyMatchPagedListAdapter.submitList(latestWorldRugbyMatches)
            worldRugbyMatchDateItemDecoration.matches = latestWorldRugbyMatches
            val isEmpty = latestWorldRugbyMatches?.isEmpty() ?: true
            progressBar.isVisible = isEmpty
        })
        viewModel.latestWorldRugbyMatchesWorkInfos.observe(viewLifecycleOwner, Observer { workInfos ->
            val workInfo = workInfos?.firstOrNull()
            when (workInfo?.state) {
                State.RUNNING -> {
                    swipeRefreshLayout.isEnabled = false
                    doIfResumed {
                        workerSnackBar = Snackbar.make(
                                coordinatorLayout,
                                R.string.snackbar_fetching_world_rugby_matches,
                                Snackbar.LENGTH_INDEFINITE
                        ).apply { show() }
                    }
                }
                else -> {
                    swipeRefreshLayout.isEnabled = true
                    root.post { workerSnackBar?.dismiss() }
                }
            }
        })
        viewModel.refreshingLatestWorldRugbyMatches.observe(viewLifecycleOwner, Observer { refreshingLatestWorldRugbyMatches ->
            swipeRefreshLayout.isRefreshing = refreshingLatestWorldRugbyMatches
        })
        viewModel.worldRugbyRankingsTeamIds.observe(viewLifecycleOwner, Observer { worldRugbyRankingsTeamIds ->
            worldRugbyMatchPagedListAdapter.worldRugbyRankingsTeamIds = worldRugbyRankingsTeamIds.associateBy({ it }, { true })
        })
        viewModel.scrollToTop.observe(viewLifecycleOwner, EventObserver {
            doIfResumed { matchesRecyclerView.smoothScrollToPosition(0) }
        })
    }

    private fun setupSwipeRefreshLayout() {
        val swipeRefreshColors = resources.getIntArray(R.array.colors_swipe_refresh)
        swipeRefreshLayout.setColorSchemeColors(*swipeRefreshColors)
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.color_surface)
        swipeRefreshLayout.setProgressViewOffset(true,
                swipeRefreshLayout.progressViewStartOffset + resources.getDimensionPixelSize(R.dimen.spacing_double),
                swipeRefreshLayout.progressViewEndOffset)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshLatestWorldRugbyMatches { success ->
                if (!success) {
                    doIfResumed {
                        Snackbar.make(
                                coordinatorLayout,
                                R.string.snackbar_failed_to_refresh_world_rugby_matches,
                                Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "MatchesFragment"
        fun newInstance(sport: Sport, matchStatus: MatchStatus): MatchesFragment {
            val matchesFragment = MatchesFragment()
            matchesFragment.arguments = MatchesNavGraphDirections.matchesFragmentAction(sport, matchStatus).arguments
            return matchesFragment
        }
    }
}
