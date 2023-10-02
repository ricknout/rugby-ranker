package dev.ricknout.rugbyranker.ui

import android.animation.LayoutTransition
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isInvisible
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionManager
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.insetter.applyInsetter
import dev.ricknout.rugbyranker.R
import dev.ricknout.rugbyranker.core.model.Sport
import dev.ricknout.rugbyranker.core.ui.openDrawer
import dev.ricknout.rugbyranker.databinding.FragmentSportBinding
import dev.ricknout.rugbyranker.live.ui.LiveMatchFragment
import dev.ricknout.rugbyranker.live.ui.LiveMatchViewModel
import dev.ricknout.rugbyranker.live.ui.MensLiveMatchViewModel
import dev.ricknout.rugbyranker.live.ui.WomensLiveMatchViewModel
import dev.ricknout.rugbyranker.match.model.Status
import dev.ricknout.rugbyranker.match.ui.MatchFragment
import dev.ricknout.rugbyranker.match.ui.MatchViewModel
import dev.ricknout.rugbyranker.match.ui.MensCompleteMatchViewModel
import dev.ricknout.rugbyranker.match.ui.MensUnplayedMatchViewModel
import dev.ricknout.rugbyranker.match.ui.WomensCompleteMatchViewModel
import dev.ricknout.rugbyranker.match.ui.WomensUnplayedMatchViewModel
import dev.ricknout.rugbyranker.prediction.model.Prediction
import dev.ricknout.rugbyranker.prediction.ui.MensPredictionViewModel
import dev.ricknout.rugbyranker.prediction.ui.PredictionBar
import dev.ricknout.rugbyranker.prediction.ui.PredictionViewModel
import dev.ricknout.rugbyranker.prediction.ui.WomensPredictionViewModel
import dev.ricknout.rugbyranker.ranking.ui.MensRankingViewModel
import dev.ricknout.rugbyranker.ranking.ui.RankingFragment
import dev.ricknout.rugbyranker.ranking.ui.RankingViewModel
import dev.ricknout.rugbyranker.ranking.ui.WomensRankingViewModel

@AndroidEntryPoint
class SportFragment : Fragment() {
    private val args: SportFragmentArgs by navArgs()

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

    private val unplayedMatchViewModel: MatchViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensUnplayedMatchViewModel>().value
            Sport.WOMENS -> activityViewModels<WomensUnplayedMatchViewModel>().value
        }
    }

    private val completeMatchViewModel: MatchViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensCompleteMatchViewModel>().value
            Sport.WOMENS -> activityViewModels<WomensCompleteMatchViewModel>().value
        }
    }

    private val liveMatchViewModel: LiveMatchViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensLiveMatchViewModel>().value
            Sport.WOMENS -> activityViewModels<WomensLiveMatchViewModel>().value
        }
    }

    private var _binding: FragmentSportBinding? = null
    private val binding get() = _binding!!

    private val transitionDuration by lazy {
        resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
    }

    private val onBackPressedCallback =
        object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                binding.viewPager.currentItem = POSITION_RANKINGS
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply { duration = transitionDuration }
        exitTransition = MaterialFadeThrough().apply { duration = transitionDuration }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        setupViewModels()
        setupNavigation()
        setupViewPagerAndTabs()
        setupFab()
        setupPredictionBar()
        setupEdgeToEdge()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupViewModels() {
        predictionViewModel.predictions.observe(
            viewLifecycleOwner,
        ) { predictions ->
            rankingViewModel.setPredictions(predictions)
            val currentPredictions = binding.predictionBar.getPredictions()
            binding.predictionBar.setPredictions(predictions)
            val shouldTransition =
                when {
                    currentPredictions.isEmpty() && !predictions.isNullOrEmpty() -> true
                    currentPredictions.isNotEmpty() && predictions.isNullOrEmpty() -> true
                    else -> false
                }
            val shouldShowRankings = currentPredictions != predictions
            if (shouldShowRankings) binding.viewPager.currentItem = POSITION_RANKINGS
            if (!shouldTransition) return@observe
            val transition =
                MaterialContainerTransform().apply {
                    duration = transitionDuration
                    interpolator = FastOutSlowInInterpolator()
                    scrimColor = Color.TRANSPARENT
                    fadeMode = MaterialContainerTransform.FADE_MODE_OUT
                }
            transition.addTarget(binding.fab)
            if (predictions.isNullOrEmpty()) {
                transition.startView = binding.predictionBar
                transition.endView = binding.fab
            } else {
                transition.startView = binding.fab
                transition.endView = binding.predictionBar
            }
            TransitionManager.beginDelayedTransition(binding.coordinatorLayout, transition)
            binding.fab.isInvisible = !predictions.isNullOrEmpty()
            binding.predictionBar.isInvisible = predictions.isNullOrEmpty()
        }
        predictionViewModel.teams.observe(
            viewLifecycleOwner,
        ) { teams ->
            binding.fab.isEnabled = !teams.isNullOrEmpty()
        }
        unplayedMatchViewModel.predict.observe(
            viewLifecycleOwner,
        ) { prediction ->
            if (prediction == null) return@observe
            val edit = predictionViewModel.containsPredictionWithId(prediction)
            findNavController().navigate(
                SportFragmentDirections.sportToPrediction(
                    sport,
                    prediction,
                    edit,
                ),
            )
            unplayedMatchViewModel.resetPredict()
        }
        liveMatchViewModel.predict.observe(
            viewLifecycleOwner,
        ) { prediction ->
            if (prediction == null) return@observe
            val edit = predictionViewModel.containsPredictionWithId(prediction)
            findNavController().navigate(
                SportFragmentDirections.sportToPrediction(
                    sport,
                    prediction,
                    edit,
                ),
            )
            liveMatchViewModel.resetPredict()
        }
        liveMatchViewModel.liveMatches.observe(
            viewLifecycleOwner,
        ) { liveMatches ->
            val show = !liveMatches.isNullOrEmpty()
            toggleLiveMatchTabIcon(show)
        }
    }

    private fun setupNavigation() {
        binding.appBar.navigation.setOnClickListener { openDrawer() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    private fun setupViewPagerAndTabs() {
        binding.viewPager.adapter = SportAdapter(this)
        TabLayoutMediator(binding.appBar.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                POSITION_RANKINGS -> tab.text = getString(R.string.rankings)
                POSITION_MATCHES_UNPLAYED -> tab.text = getString(R.string.fixtures)
                POSITION_MATCHES_COMPLETE -> tab.text = getString(R.string.results)
                POSITION_MATCHES_LIVE -> {
                    tab.text = getString(R.string.live)
                    // Animate showing/hiding of live tab icon
                    tab.view.layoutTransition = LayoutTransition()
                }
                else -> throw IllegalArgumentException("Position $position exceeds SportAdapter count")
            }
        }.attach()
        binding.appBar.tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    onBackPressedCallback.isEnabled = tab?.position != POSITION_RANKINGS
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    binding.appBar.appBarLayout.isLifted = false
                    when (tab?.position) {
                        POSITION_RANKINGS -> rankingViewModel.scrollToTop()
                        POSITION_MATCHES_UNPLAYED -> unplayedMatchViewModel.scrollToTop()
                        POSITION_MATCHES_COMPLETE -> completeMatchViewModel.scrollToTop()
                        POSITION_MATCHES_LIVE -> liveMatchViewModel.scrollToTop()
                    }
                }
            },
        )
    }

    private fun setupFab() {
        TooltipCompat.setTooltipText(binding.fab, getString(R.string.add_prediction))
        binding.fab.setOnClickListener {
            findNavController().navigate(SportFragmentDirections.sportToPrediction(sport))
        }
    }

    private fun setupPredictionBar() {
        binding.predictionBar.listener =
            object : PredictionBar.PredictionBarListener {
                override fun onAddPredictionClick() {
                    findNavController().navigate(SportFragmentDirections.sportToPrediction(sport))
                }

                override fun onPredictionClick(prediction: Prediction) {
                    findNavController().navigate(SportFragmentDirections.sportToPrediction(sport, prediction, edit = true))
                }

                override fun onRemovePredictionClick(prediction: Prediction) {
                    predictionViewModel.removePrediction(prediction)
                }
            }
    }

    private fun toggleLiveMatchTabIcon(show: Boolean) {
        val tab = binding.appBar.tabLayout.getTabAt(POSITION_MATCHES_LIVE) ?: return
        if (show) {
            val dotAvd = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.avd_dot_24dp)
            tab.icon = dotAvd
            dotAvd?.start()
            tab.view.updatePaddingRelative(start = resources.getDimensionPixelSize(R.dimen.space_tab_live))
        } else {
            if (tab.icon != null) {
                tab.icon = null
                tab.view.updatePaddingRelative(start = resources.getDimensionPixelSize(R.dimen.space))
            }
        }
    }

    private fun setupEdgeToEdge() {
        binding.appBar.appBarLayout.applyInsetter {
            type(statusBars = true, navigationBars = true) {
                padding(horizontal = true, top = true)
            }
        }
        binding.fab.applyInsetter {
            type(navigationBars = true) {
                margin()
            }
        }
        binding.predictionBar.applyInsetter {
            type(navigationBars = true) {
                margin()
            }
        }
    }

    inner class SportAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int) =
            when (position) {
                POSITION_RANKINGS -> RankingFragment.newInstance(sport)
                POSITION_MATCHES_UNPLAYED -> MatchFragment.newInstance(sport, Status.UNPLAYED)
                POSITION_MATCHES_COMPLETE -> MatchFragment.newInstance(sport, Status.COMPLETE)
                POSITION_MATCHES_LIVE -> LiveMatchFragment.newInstance(sport)
                else -> throw IllegalArgumentException("Position $position exceeds SportAdapter count")
            }

        override fun getItemCount() = 4
    }

    companion object {
        private const val POSITION_RANKINGS = 0
        private const val POSITION_MATCHES_UNPLAYED = 1
        private const val POSITION_MATCHES_COMPLETE = 2
        private const val POSITION_MATCHES_LIVE = 3
    }
}
