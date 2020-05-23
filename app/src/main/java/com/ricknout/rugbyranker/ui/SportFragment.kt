package com.ricknout.rugbyranker.ui

import android.animation.LayoutTransition
import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialContainerTransform
import com.ricknout.rugbyranker.R
import com.ricknout.rugbyranker.core.livedata.EventObserver
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.live.ui.LiveMatchesFragment
import com.ricknout.rugbyranker.live.ui.LiveMatchesViewModel
import com.ricknout.rugbyranker.live.ui.MensLiveMatchesViewModel
import com.ricknout.rugbyranker.live.ui.WomensLiveMatchesViewModel
import com.ricknout.rugbyranker.matches.ui.MatchesFragment
import com.ricknout.rugbyranker.matches.ui.MatchesViewModel
import com.ricknout.rugbyranker.matches.ui.MensCompleteMatchesViewModel
import com.ricknout.rugbyranker.matches.ui.MensUnplayedMatchesViewModel
import com.ricknout.rugbyranker.matches.ui.WomensCompleteMatchesViewModel
import com.ricknout.rugbyranker.matches.ui.WomensUnplayedMatchesViewModel
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch
import com.ricknout.rugbyranker.prediction.ui.MensPredictionViewModel
import com.ricknout.rugbyranker.prediction.ui.PredictionBarView
import com.ricknout.rugbyranker.prediction.ui.PredictionViewModel
import com.ricknout.rugbyranker.prediction.ui.WomensPredictionViewModel
import com.ricknout.rugbyranker.prediction.vo.Prediction
import com.ricknout.rugbyranker.rankings.ui.MensRankingsViewModel
import com.ricknout.rugbyranker.rankings.ui.RankingsFragment
import com.ricknout.rugbyranker.rankings.ui.RankingsViewModel
import com.ricknout.rugbyranker.rankings.ui.WomensRankingsViewModel
import com.ricknout.rugbyranker.teams.ui.MensTeamsViewModel
import com.ricknout.rugbyranker.teams.ui.TeamsViewModel
import com.ricknout.rugbyranker.teams.ui.WomensTeamsViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import kotlinx.android.synthetic.main.fragment_sport.*

class SportFragment : DaggerFragment(R.layout.fragment_sport) {

    private val args: SportFragmentArgs by navArgs()

    private val sport: Sport by lazy { args.sport }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val sportViewModel: SportViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensViewModel> { viewModelFactory }.value
            Sport.WOMENS -> activityViewModels<WomensViewModel> { viewModelFactory }.value
        }
    }
    private val rankingsViewModel: RankingsViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensRankingsViewModel> { viewModelFactory }.value
            Sport.WOMENS -> activityViewModels<WomensRankingsViewModel> { viewModelFactory }.value
        }
    }
    private val predictionViewModel: PredictionViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensPredictionViewModel> { viewModelFactory }.value
            Sport.WOMENS -> activityViewModels<WomensPredictionViewModel> { viewModelFactory }.value
        }
    }
    private val teamsViewModel: TeamsViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensTeamsViewModel> { viewModelFactory }.value
            Sport.WOMENS -> activityViewModels<WomensTeamsViewModel> { viewModelFactory }.value
        }
    }
    private val liveMatchesViewModel: LiveMatchesViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensLiveMatchesViewModel> { viewModelFactory }.value
            Sport.WOMENS -> activityViewModels<WomensLiveMatchesViewModel> { viewModelFactory }.value
        }
    }
    private val unplayedMatchesViewModel: MatchesViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensUnplayedMatchesViewModel> { viewModelFactory }.value
            Sport.WOMENS -> activityViewModels<WomensUnplayedMatchesViewModel> { viewModelFactory }.value
        }
    }
    private val completedMatchesViewModel: MatchesViewModel by lazy {
        when (sport) {
            Sport.MENS -> activityViewModels<MensCompleteMatchesViewModel> { viewModelFactory }.value
            Sport.WOMENS -> activityViewModels<WomensCompleteMatchesViewModel> { viewModelFactory }.value
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setTitle()
        setupTabsAndViewPager()
        setupAddPredictionFab()
        setupPredictionBarView()
        setupViewModels()
    }

    private fun setTitle() {
        titleTextView.setText(when (sport) {
            Sport.MENS -> R.string.title_mens_rugby_rankings
            Sport.WOMENS -> R.string.title_womens_rugby_rankings
        })
    }

    private fun setSubtitle(effectiveTime: String?) {
        when {
            effectiveTime != null -> {
                subtitleTextView.text = getString(R.string.subtitle_last_updated, effectiveTime)
                subtitleTextView.isVisible = true
            }
            predictionViewModel.hasPredictions() -> {
                val predictionCount = predictionViewModel.getPredictionCount()
                subtitleTextView.text = resources.getQuantityString(R.plurals.subtitle_predicting_matches, predictionCount, predictionCount)
                subtitleTextView.isVisible = true
            }
            else -> {
                subtitleTextView.text = null
                subtitleTextView.isVisible = false
            }
        }
    }

    private fun setupTabsAndViewPager() {
        viewPager.adapter = SportFragmentStateAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                POSITION_RANKINGS -> getString(R.string.title_rankings)
                POSITION_LIVE -> getString(R.string.title_live)
                POSITION_FIXTURES -> getString(R.string.title_fixtures)
                POSITION_RESULTS -> getString(R.string.title_results)
                else -> null
            }
        }.attach()
        // Animate showing/hiding of live tab icon
        tabLayout.getTabAt(POSITION_LIVE)?.view?.layoutTransition = LayoutTransition()
    }

    private fun toggleLiveMatchesTabIcon(show: Boolean) {
        val tab = tabLayout.getTabAt(POSITION_LIVE) ?: return
        if (show) {
            val dotAvd = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.avd_dot_black_24dp)
            tab.icon = dotAvd
            dotAvd?.start()
        } else {
            if (tab.icon != null) tab.icon = null
        }
    }

    private fun setupAddPredictionFab() {
        addPredictionFab.setOnClickListener {
            navigateToPrediction()
        }
        TooltipCompat.setTooltipText(addPredictionFab, getString(R.string.tooltip_add_match_prediction))
    }

    private fun setupPredictionBarView() {
        predictionBarView.listener = object : PredictionBarView.PredictionBarViewListener {

            override fun onAddPredictionClick() {
                navigateToPrediction()
            }

            override fun onPredictionClick(prediction: Prediction) {
                navigateToPrediction(isEditing = true, prediction = prediction)
            }

            override fun onPredictionRemoveClick(prediction: Prediction) {
                predictionViewModel.removePrediction(prediction)
            }
        }
    }

    private fun setupViewModels() {
        sportViewModel.scrollToTop.observe(viewLifecycleOwner, EventObserver {
            appBarLayout.setExpanded(true)
        })
        rankingsViewModel.latestWorldRugbyRankingsEffectiveTime.observe(viewLifecycleOwner, Observer { effectiveTime ->
            setSubtitle(effectiveTime)
        })
        rankingsViewModel.onScroll.observe(viewLifecycleOwner, EventObserver { delta ->
            if (delta > 0) addPredictionFab.shrink() else addPredictionFab.extend()
        })
        predictionViewModel.predictions.observe(viewLifecycleOwner, Observer { predictions ->
            rankingsViewModel.predictions.value = predictions
            val currentPredictions = predictionBarView.getPredictions()
            predictionBarView.setPredictions(predictions)
            val shouldTransition = when {
                currentPredictions.isEmpty() && !predictions.isNullOrEmpty() -> true
                currentPredictions.isNotEmpty() && predictions.isNullOrEmpty() -> true
                else -> false
            }
            if (!shouldTransition) return@Observer
            val transition = MaterialContainerTransform().apply {
                duration = 400L
                interpolator = FastOutSlowInInterpolator()
                scrimColor = Color.TRANSPARENT
                fadeMode = MaterialContainerTransform.FADE_MODE_OUT
            }
            if (predictions.isNullOrEmpty()) {
                transition.startView = predictionBarView.getCardView()
                transition.endView = addPredictionFab
            } else {
                transition.startView = addPredictionFab
                transition.endView = predictionBarView.getCardView()
            }
            TransitionManager.beginDelayedTransition(coordinatorLayout, transition)
            addPredictionFab.isInvisible = !predictions.isNullOrEmpty()
            predictionBarView.getCardView().isInvisible = predictions.isNullOrEmpty()
        })
        teamsViewModel.latestWorldRugbyTeams.observe(viewLifecycleOwner, Observer { latestWorldRugbyTeams ->
            val isEmpty = latestWorldRugbyTeams?.isEmpty() ?: true
            addPredictionFab.isEnabled = !isEmpty
        })
        liveMatchesViewModel.liveWorldRugbyMatches.observe(viewLifecycleOwner, Observer { liveWorldRugbyMatches ->
            val show = !liveWorldRugbyMatches.isNullOrEmpty()
            toggleLiveMatchesTabIcon(show)
        })
        liveMatchesViewModel.navigatePredict.observe(viewLifecycleOwner, EventObserver { worldRugbyMatch ->
            val prediction = getPredictionFromWorldRugbyMatch(worldRugbyMatch)
            navigateToPrediction(prediction = prediction)
        })
        liveMatchesViewModel.onScroll.observe(viewLifecycleOwner, EventObserver { delta ->
            if (delta > 0) addPredictionFab.shrink() else addPredictionFab.extend()
        })
        unplayedMatchesViewModel.navigatePredict.observe(viewLifecycleOwner, EventObserver { worldRugbyMatch ->
            val prediction = getPredictionFromWorldRugbyMatch(worldRugbyMatch)
            navigateToPrediction(prediction = prediction)
        })
        unplayedMatchesViewModel.onScroll.observe(viewLifecycleOwner, EventObserver { delta ->
            if (delta > 0) addPredictionFab.shrink() else addPredictionFab.extend()
        })
        completedMatchesViewModel.onScroll.observe(viewLifecycleOwner, EventObserver { delta ->
            if (delta > 0) addPredictionFab.shrink() else addPredictionFab.extend()
        })
    }

    private fun getPredictionFromWorldRugbyMatch(worldRugbyMatch: WorldRugbyMatch): Prediction {
        val switched = worldRugbyMatch.secondTeamName == worldRugbyMatch.venueCountry
        val homeTeamId = if (!switched) worldRugbyMatch.firstTeamId else worldRugbyMatch.secondTeamId
        val homeTeamName = if (!switched) worldRugbyMatch.firstTeamName else worldRugbyMatch.secondTeamName
        val homeTeamAbbreviation = if (!switched) worldRugbyMatch.firstTeamAbbreviation!! else worldRugbyMatch.secondTeamAbbreviation!!
        val homeTeamScore = when {
            worldRugbyMatch.status == MatchStatus.UNPLAYED -> Prediction.NO_SCORE
            worldRugbyMatch.status == MatchStatus.POSTPONED -> Prediction.NO_SCORE
            !switched -> worldRugbyMatch.firstTeamScore
            else -> worldRugbyMatch.secondTeamScore
        }
        val awayTeamId = if (!switched) worldRugbyMatch.secondTeamId else worldRugbyMatch.firstTeamId
        val awayTeamName = if (!switched) worldRugbyMatch.secondTeamName else worldRugbyMatch.firstTeamName
        val awayTeamAbbreviation = if (!switched) worldRugbyMatch.secondTeamAbbreviation!! else worldRugbyMatch.firstTeamAbbreviation!!
        val awayTeamScore = when {
            worldRugbyMatch.status == MatchStatus.UNPLAYED -> Prediction.NO_SCORE
            worldRugbyMatch.status == MatchStatus.POSTPONED -> Prediction.NO_SCORE
            !switched -> worldRugbyMatch.secondTeamScore
            else -> worldRugbyMatch.firstTeamScore
        }
        val noHomeAdvantage = worldRugbyMatch.venueCountry?.let { venueCountry ->
            venueCountry != worldRugbyMatch.firstTeamName && venueCountry != worldRugbyMatch.secondTeamName
        } ?: false
        val rugbyWorldCup = worldRugbyMatch.eventLabel?.let { eventLabel ->
            eventLabel.contains("Rugby World Cup", ignoreCase = true) && !eventLabel.contains("Qualifying", ignoreCase = true)
        } ?: false
        return Prediction(
                id = Prediction.generateId(),
                homeTeamId = homeTeamId,
                homeTeamName = homeTeamName,
                homeTeamAbbreviation = homeTeamAbbreviation,
                homeTeamScore = homeTeamScore,
                awayTeamId = awayTeamId,
                awayTeamName = awayTeamName,
                awayTeamAbbreviation = awayTeamAbbreviation,
                awayTeamScore = awayTeamScore,
                noHomeAdvantage = noHomeAdvantage,
                rugbyWorldCup = rugbyWorldCup
        )
    }

    private fun navigateToPrediction(isEditing: Boolean = false, prediction: Prediction? = null) {
        viewPager.currentItem = POSITION_RANKINGS
        findNavController().navigate(SportFragmentDirections.sportFragmentToPredictionBottomSheetDialogFragment(
                sport = sport, isEditing = isEditing, prediction = prediction))
    }

    inner class SportFragmentStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun createFragment(position: Int) = when (position) {
            POSITION_RANKINGS -> RankingsFragment.newInstance(sport)
            POSITION_LIVE -> LiveMatchesFragment.newInstance(sport)
            POSITION_FIXTURES -> MatchesFragment.newInstance(sport, MatchStatus.UNPLAYED)
            POSITION_RESULTS -> MatchesFragment.newInstance(sport, MatchStatus.COMPLETE)
            else -> throw IllegalArgumentException("Position $position exceeds SportFragmentPagerAdapter count")
        }

        override fun getItemCount() = 4
    }

    companion object {
        const val TAG = "SportFragment"
        private const val POSITION_RANKINGS = 0
        private const val POSITION_LIVE = 1
        private const val POSITION_FIXTURES = 2
        private const val POSITION_RESULTS = 3
    }
}
