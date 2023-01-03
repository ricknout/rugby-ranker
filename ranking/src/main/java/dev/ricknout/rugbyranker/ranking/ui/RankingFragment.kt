package dev.ricknout.rugbyranker.ranking.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.work.WorkInfo
import com.google.android.material.color.MaterialColors
import com.google.android.material.elevation.ElevationOverlayProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.core.util.DateUtils
import dev.ricknout.rugbyranker.prediction.ui.MensPredictionViewModel
import dev.ricknout.rugbyranker.prediction.ui.PredictionViewModel
import dev.ricknout.rugbyranker.prediction.ui.WomensPredictionViewModel
import dev.ricknout.rugbyranker.ranking.R
import dev.ricknout.rugbyranker.ranking.databinding.FragmentRankingBinding

@AndroidEntryPoint
class RankingFragment : Fragment() {

    private val args: RankingFragmentArgs by navArgs()

    private val sport: Sport by lazy { args.sport }

    private val rankingViewModel: RankingViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensRankingViewModel>().value
            Sport.WOMENS -> activityViewModels<WomensRankingViewModel>().value
        }
    }

    private val predictionViewModel: PredictionViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensPredictionViewModel>().value
            Sport.WOMENS -> activityViewModels<WomensPredictionViewModel>().value
        }
    }

    private val rankingAdapter = RankingAdapter()
    private val labelAdapter = LabelAdapter()
    private val adapter = ConcatAdapter(rankingAdapter, labelAdapter)

    private val coordinatorLayout: CoordinatorLayout
        get() = ActivityCompat.requireViewById(requireActivity(), R.id.coordinatorLayout)

    private val fab: FloatingActionButton
        get() = ActivityCompat.requireViewById(requireActivity(), R.id.fab)

    private var _binding: FragmentRankingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRankingBinding.inflate(inflater, container, false)
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
        rankingViewModel.rankings.observe(
            viewLifecycleOwner,
            { pair ->
                val rankings = pair.first
                val updatedTimeMillis = pair.second
                rankingAdapter.submitList(rankings) {
                    setLabel(updatedTimeMillis)
                }
                if (rankings.isNullOrEmpty()) binding.progressIndicator.show() else binding.progressIndicator.hide()
            },
        )
        rankingViewModel.refreshingRankings.observe(
            viewLifecycleOwner,
            { refreshingRankings ->
                binding.swipeRefreshLayout.isRefreshing = refreshingRankings
            },
        )
        rankingViewModel.workInfos.observe(
            viewLifecycleOwner,
            { workInfos ->
                val workInfo = workInfos?.firstOrNull()
                binding.swipeRefreshLayout.isEnabled = workInfo?.state != WorkInfo.State.RUNNING
            },
        )
        rankingViewModel.scrollToTop.observe(
            viewLifecycleOwner,
            { scrollToTop ->
                if (scrollToTop) {
                    binding.recyclerView.smoothScrollToPosition(0)
                    rankingViewModel.resetScrollToTop()
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
        binding.swipeRefreshLayout.setOnRefreshListener {
            rankingViewModel.refreshRankings { success ->
                if (!success) {
                    Snackbar.make(
                        coordinatorLayout,
                        R.string.failed_to_refresh_rankings,
                        Snackbar.LENGTH_SHORT,
                    ).apply {
                        anchorView = fab
                        show()
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
    }

    private fun setLabel(updatedTimeMillis: Long?) {
        when {
            updatedTimeMillis != null -> {
                val isCurrentDay = DateUtils.isDayCurrentDay(updatedTimeMillis)
                val updatedTime = if (isCurrentDay) {
                    getString(R.string.today)
                } else {
                    DateUtils.getDate(DateUtils.DATE_FORMAT_D_MMM_YYYY, updatedTimeMillis)
                }
                labelAdapter.submitList(listOf(getString(R.string.updated, updatedTime)))
            }
            predictionViewModel.hasPredictions() -> {
                val predictionCount = predictionViewModel.getPredictionCount()
                labelAdapter.submitList(listOf(resources.getQuantityString(R.plurals.predicting, predictionCount, predictionCount)))
            }
        }
    }

    private fun setupEdgeToEdge() {
        binding.recyclerView.applyInsetter {
            type(navigationBars = true) {
                padding()
            }
        }
    }

    companion object {
        fun newInstance(sport: Sport): RankingFragment {
            val rankingFragment = RankingFragment()
            rankingFragment.arguments = RankingFragmentDirections.rankingAction(sport).arguments
            return rankingFragment
        }
    }
}
