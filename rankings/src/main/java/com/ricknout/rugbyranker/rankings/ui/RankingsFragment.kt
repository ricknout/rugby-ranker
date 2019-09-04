package com.ricknout.rugbyranker.rankings.ui

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo.State
import com.google.android.material.snackbar.Snackbar
import com.ricknout.rugbyranker.core.livedata.EventObserver
import com.ricknout.rugbyranker.core.ui.dagger.DaggerAndroidXFragment
import com.ricknout.rugbyranker.core.util.doIfResumed
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.rankings.R
import com.ricknout.rugbyranker.rankings.RankingsNavGraphDirections
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_rankings.*

class RankingsFragment : DaggerAndroidXFragment(R.layout.fragment_rankings) {

    private val args: RankingsFragmentArgs by navArgs()

    private val sport: Sport by lazy { args.sport }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: RankingsViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensRankingsViewModel> { viewModelFactory }.value
            Sport.WOMENS -> activityViewModels<WomensRankingsViewModel> { viewModelFactory }.value
        }
    }

    private val coordinatorLayout by lazy {
        ActivityCompat.requireViewById<CoordinatorLayout>(requireActivity(), R.id.coordinatorLayout)
    }

    private var workerSnackBar: Snackbar? = null

    private val worldRugbyRankingAdapter = WorldRugbyRankingListAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setupViewModel()
        setupSwipeRefreshLayout()
    }

    private fun setupRecyclerView() {
        val dividerItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        rankingsRecyclerView.addItemDecoration(dividerItemDecoration, 0)
        rankingsRecyclerView.adapter = worldRugbyRankingAdapter
        rankingsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                viewModel.onScroll(delta = dy)
            }
        })
    }

    private fun setupViewModel() {
        viewModel.worldRugbyRankings.observe(viewLifecycleOwner, Observer { worldRugbyRankings ->
            worldRugbyRankingAdapter.submitList(worldRugbyRankings)
            val isEmpty = worldRugbyRankings?.isEmpty() ?: true
            progressBar.isVisible = isEmpty
        })
        viewModel.latestWorldRugbyRankingsWorkInfos.observe(viewLifecycleOwner, Observer { workInfos ->
            val workInfo = workInfos?.firstOrNull()
            when (workInfo?.state) {
                State.RUNNING -> {
                    swipeRefreshLayout.isEnabled = false
                    doIfResumed {
                        workerSnackBar = Snackbar.make(
                                coordinatorLayout,
                                R.string.snackbar_fetching_world_rugby_rankings,
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
        viewModel.refreshingLatestWorldRugbyRankings.observe(viewLifecycleOwner, Observer { refreshingLatestWorldRugbyRankings ->
            swipeRefreshLayout.isRefreshing = refreshingLatestWorldRugbyRankings
        })
        viewModel.scrollToTop.observe(viewLifecycleOwner, EventObserver {
            doIfResumed { rankingsRecyclerView.smoothScrollToPosition(0) }
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
            viewModel.refreshLatestWorldRugbyRankings { success ->
                if (!success) {
                    doIfResumed {
                        Snackbar.make(
                                coordinatorLayout,
                                R.string.snackbar_failed_to_refresh_world_rugby_rankings,
                                Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "RankingsFragment"
        fun newInstance(sport: Sport): RankingsFragment {
            val rankingsFragment = RankingsFragment()
            rankingsFragment.arguments = RankingsNavGraphDirections.rankingsFragmentAction(sport).arguments
            return rankingsFragment
        }
    }
}
