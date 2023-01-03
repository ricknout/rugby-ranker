package dev.ricknout.rugbyranker.match.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import com.google.android.material.color.MaterialColors
import com.google.android.material.elevation.ElevationOverlayProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.match.R
import dev.ricknout.rugbyranker.match.databinding.FragmentMatchBinding
import dev.ricknout.rugbyranker.match.model.Status

@AndroidEntryPoint
class MatchFragment : Fragment() {

    private val args: MatchFragmentArgs by navArgs()

    private val sport: Sport by lazy { args.sport }
    private val status: Status by lazy { args.status }

    private val matchViewModel: MatchViewModel by lazy {
        when {
            sport == Sport.MENS && status == Status.UNPLAYED -> activityViewModels<MensUnplayedMatchViewModel>().value
            sport == Sport.MENS && status == Status.COMPLETE -> activityViewModels<MensCompleteMatchViewModel>().value
            sport == Sport.WOMENS && status == Status.UNPLAYED -> activityViewModels<WomensUnplayedMatchViewModel>().value
            sport == Sport.WOMENS && status == Status.COMPLETE -> activityViewModels<WomensCompleteMatchViewModel>().value
            else -> throw IllegalArgumentException("Cannot handle $sport and $status in MatchFragment")
        }
    }

    private val adapter = MatchAdapter { match ->
        val prediction = match.toPrediction()
        matchViewModel.predict(prediction)
    }

    private val coordinatorLayout: CoordinatorLayout
        get() = ActivityCompat.requireViewById(requireActivity(), R.id.coordinatorLayout)

    private val fab: FloatingActionButton
        get() = ActivityCompat.requireViewById(requireActivity(), R.id.fab)

    private var _binding: FragmentMatchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupSwipeRefresh()
        setupRecyclerView()
        setupEdgeToEdge()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupViewModel() {
        matchViewModel.matches.observe(
            viewLifecycleOwner,
            { pagingData ->
                adapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
            },
        )
        matchViewModel.scrollToTop.observe(
            viewLifecycleOwner,
            { scrollToTop ->
                if (scrollToTop) {
                    binding.recyclerView.smoothScrollToPosition(0)
                    matchViewModel.resetScrollToTop()
                }
            },
        )
    }

    private fun setupSwipeRefresh() {
        // Prevent AppBarLayout#liftOnScroll flickering in parent SportFragment
        binding.swipeRefreshLayout.isNestedScrollingEnabled = false
        val primaryColor = MaterialColors.getColor(binding.swipeRefreshLayout, R.attr.colorPrimary)
        val elevationOverlayProvider = ElevationOverlayProvider(requireContext())
        val surfaceColor = elevationOverlayProvider.compositeOverlayWithThemeSurfaceColorIfNeeded(
            resources.getDimension(R.dimen.elevation_swipe_refresh_layout),
        )
        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeColor(surfaceColor)
        binding.swipeRefreshLayout.setColorSchemeColors(primaryColor)
        binding.swipeRefreshLayout.setProgressViewOffset(
            true,
            binding.swipeRefreshLayout.progressViewStartOffset,
            binding.swipeRefreshLayout.progressViewEndOffset,
        )
        binding.swipeRefreshLayout.setOnRefreshListener { adapter.refresh() }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
        adapter.addLoadStateListener { combinedLoadStates ->
            when (combinedLoadStates.refresh) {
                is LoadState.Loading -> {
                    binding.retry.isVisible = false
                    if (adapter.itemCount == 0) binding.progressIndicator.show() else binding.progressIndicator.hide()
                }
                is LoadState.NotLoading -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.retry.isVisible = false
                    binding.progressIndicator.hide()
                }
                is LoadState.Error -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.retry.isVisible = adapter.itemCount == 0
                    binding.progressIndicator.hide()
                    if (adapter.itemCount > 0) {
                        Snackbar.make(
                            coordinatorLayout,
                            R.string.failed_to_refresh_matches,
                            Snackbar.LENGTH_SHORT,
                        ).apply {
                            anchorView = fab
                            show()
                        }
                    }
                }
            }
        }
        binding.retryButton.setOnClickListener { adapter.retry() }
    }

    private fun setupEdgeToEdge() {
        binding.recyclerView.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }
    }

    companion object {
        fun newInstance(sport: Sport, status: Status): MatchFragment {
            val matchFragment = MatchFragment()
            matchFragment.arguments = MatchFragmentDirections.matchAction(sport, status).arguments
            return matchFragment
        }
    }
}
