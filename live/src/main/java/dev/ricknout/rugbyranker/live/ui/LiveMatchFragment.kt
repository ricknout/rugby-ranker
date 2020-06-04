package dev.ricknout.rugbyranker.live.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.material.color.MaterialColors
import com.google.android.material.elevation.ElevationOverlayProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applySystemWindowInsetsToPadding
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.live.R
import dev.ricknout.rugbyranker.live.databinding.FragmentLiveMatchBinding

@AndroidEntryPoint
class LiveMatchFragment : Fragment() {

    private val args: LiveMatchFragmentArgs by navArgs()

    private val sport: Sport by lazy { args.sport }

    private val liveMatchViewModel: LiveMatchViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensLiveMatchViewModel>().value
            Sport.WOMENS -> activityViewModels<WomensLiveMatchViewModel>().value
        }
    }

    private val adapter = LiveMatchAdapter { match ->
        val prediction = match.toPrediction()
        liveMatchViewModel.predict(prediction)
    }

    private val coordinatorLayout: CoordinatorLayout
        get() = ActivityCompat.requireViewById(requireActivity(), R.id.coordinatorLayout)

    private val fab: FloatingActionButton
        get() = ActivityCompat.requireViewById(requireActivity(), R.id.fab)

    private var _binding: FragmentLiveMatchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLiveMatchBinding.inflate(inflater, container, false)
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
        liveMatchViewModel.liveMatches.observe(
            viewLifecycleOwner,
            Observer { liveMatches ->
                adapter.submitList(liveMatches)
                if (liveMatches == null) binding.progressIndicator.show() else binding.progressIndicator.hide()
                binding.noLiveMatches.isVisible = liveMatches?.isEmpty() ?: false
            }
        )
        liveMatchViewModel.refreshingLiveMatches.observe(
            viewLifecycleOwner,
            Observer { refreshingLiveMatches ->
                binding.swipeRefreshLayout.isRefreshing = refreshingLiveMatches
            }
        )
        liveMatchViewModel.scrollToTop.observe(
            viewLifecycleOwner,
            Observer { scrollToTop ->
                if (scrollToTop) {
                    binding.recyclerView.smoothScrollToPosition(0)
                    liveMatchViewModel.resetScrollToTop()
                }
            }
        )
    }

    private fun setupSwipeRefresh() {
        // Prevent AppBarLayout#liftOnScroll flickering in parent SportFragment
        binding.swipeRefreshLayout.isNestedScrollingEnabled = false
        val primaryColor = MaterialColors.getColor(binding.swipeRefreshLayout, dev.ricknout.rugbyranker.match.R.attr.colorPrimary)
        val elevationOverlayProvider = ElevationOverlayProvider(requireContext())
        val surfaceColor = elevationOverlayProvider.compositeOverlayWithThemeSurfaceColorIfNeeded(
            resources.getDimension(dev.ricknout.rugbyranker.match.R.dimen.elevation_swipe_refresh_layout)
        )
        binding.swipeRefreshLayout.setProgressBackgroundColorSchemeColor(surfaceColor)
        binding.swipeRefreshLayout.setColorSchemeColors(primaryColor)
        binding.swipeRefreshLayout.setProgressViewOffset(
            true,
            binding.swipeRefreshLayout.progressViewStartOffset, binding.swipeRefreshLayout.progressViewEndOffset
        )
        binding.swipeRefreshLayout.setOnRefreshListener {
            liveMatchViewModel.refreshLiveMatches { success ->
                if (!success) {
                    Snackbar.make(
                        coordinatorLayout,
                        R.string.failed_to_refresh_live_matches,
                        Snackbar.LENGTH_SHORT
                    ).apply {
                        anchorView = fab
                        show()
                    }
                }
            }
        }
    }

    private fun setupEdgeToEdge() {
        binding.recyclerView.applySystemWindowInsetsToPadding(left = true, right = true, bottom = true)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
    }

    companion object {
        fun newInstance(sport: Sport): LiveMatchFragment {
            val liveMatchFragment = LiveMatchFragment()
            liveMatchFragment.arguments = LiveMatchFragmentDirections.liveMatchAction(sport).arguments
            return liveMatchFragment
        }
    }
}
