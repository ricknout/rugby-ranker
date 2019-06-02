package com.ricknout.rugbyranker.ui

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.getSystemService
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.emoji.text.EmojiCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ricknout.rugbyranker.R
import com.ricknout.rugbyranker.core.livedata.EventObserver
import com.ricknout.rugbyranker.core.ui.dagger.DaggerAndroidXFragment
import com.ricknout.rugbyranker.matches.ui.MatchesFragment
import com.ricknout.rugbyranker.matches.ui.MatchesViewModel
import com.ricknout.rugbyranker.matches.ui.MensUnplayedMatchesViewModel
import com.ricknout.rugbyranker.matches.ui.WomensUnplayedMatchesViewModel
import com.ricknout.rugbyranker.rankings.ui.RankingsViewModel
import com.ricknout.rugbyranker.rankings.ui.MensRankingsViewModel
import com.ricknout.rugbyranker.rankings.ui.WomensRankingsViewModel
import com.ricknout.rugbyranker.rankings.ui.RankingsFragment
import com.ricknout.rugbyranker.core.util.FlagUtils
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.core.vo.Sport
import com.ricknout.rugbyranker.live.ui.LiveMatchesFragment
import com.ricknout.rugbyranker.live.ui.LiveMatchesViewModel
import com.ricknout.rugbyranker.live.ui.MensLiveMatchesViewModel
import com.ricknout.rugbyranker.live.ui.WomensLiveMatchesViewModel
import com.ricknout.rugbyranker.prediction.ui.MatchPredictionInputView
import com.ricknout.rugbyranker.prediction.vo.MatchPrediction
import com.ricknout.rugbyranker.rankings.vo.WorldRugbyRanking
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch
import kotlinx.android.synthetic.main.fragment_sport.*
import java.lang.IllegalArgumentException
import javax.inject.Inject

class SportFragment : DaggerAndroidXFragment(R.layout.fragment_sport) {

    private val args: SportFragmentArgs by navArgs()

    private val sport: Sport by lazy { args.sport }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val sportViewModel: SportViewModel by lazy {
        when (sport) {
            Sport.MENS -> viewModels<MensViewModel>({ requireActivity() }, { viewModelFactory }).value
            Sport.WOMENS -> viewModels<WomensViewModel>({ requireActivity() }, { viewModelFactory }).value
        }
    }
    private val rankingsViewModel: RankingsViewModel by lazy {
        when (sport) {
            Sport.MENS -> viewModels<MensRankingsViewModel>({ requireActivity() }, { viewModelFactory }).value
            Sport.WOMENS -> viewModels<WomensRankingsViewModel>({ requireActivity() }, { viewModelFactory }).value
        }
    }
    private val liveMatchesViewModel: LiveMatchesViewModel by lazy {
        when (sport) {
            Sport.MENS -> viewModels<MensLiveMatchesViewModel>({ requireActivity() }, { viewModelFactory }).value
            Sport.WOMENS -> viewModels<WomensLiveMatchesViewModel>({ requireActivity() }, { viewModelFactory }).value
        }
    }
    private val unplayedMatchesViewModel: MatchesViewModel by lazy {
        when (sport) {
            Sport.MENS -> viewModels<MensUnplayedMatchesViewModel>({ requireActivity() }, { viewModelFactory }).value
            Sport.WOMENS -> viewModels<WomensUnplayedMatchesViewModel>({ requireActivity() }, { viewModelFactory }).value
        }
    }

    private var homeTeamId: Long? = null
    private var homeTeamName: String? = null
    private var homeTeamAbbreviation: String? = null
    private var awayTeamId: Long? = null
    private var awayTeamName: String? = null
    private var awayTeamAbbreviation: String? = null

    private var clearMatchPredictionInput = false
    private var showMatchPredictionInput = false

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        homeTeamId = savedInstanceState?.getLong(KEY_HOME_TEAM_ID)
        homeTeamName = savedInstanceState?.getString(KEY_HOME_TEAM_NAME)
        homeTeamAbbreviation = savedInstanceState?.getString(KEY_HOME_TEAM_ABBREVIATION)
        awayTeamId = savedInstanceState?.getLong(KEY_AWAY_TEAM_ID)
        awayTeamName = savedInstanceState?.getString(KEY_AWAY_TEAM_NAME)
        awayTeamAbbreviation = savedInstanceState?.getString(KEY_AWAY_TEAM_ABBREVIATION)
        val bottomSheetState = savedInstanceState?.getInt(KEY_BOTTOM_SHEET_STATE)
        setTitle()
        setupTabsAndViewPager()
        setupAddMatchFab()
        setupBottomSheet(bottomSheetState)
        setupViewModels()
        val onBackPressCallbackEnabled = ::bottomSheetBehavior.isInitialized && bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED
        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, enabled = onBackPressCallbackEnabled) {
            hideBottomSheet()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        homeTeamId?.let { homeTeamId ->
            outState.putLong(KEY_HOME_TEAM_ID, homeTeamId)
        }
        outState.putString(KEY_HOME_TEAM_NAME, homeTeamName)
        outState.putString(KEY_HOME_TEAM_ABBREVIATION, homeTeamAbbreviation)
        awayTeamId?.let { awayTeamId ->
            outState.putLong(KEY_AWAY_TEAM_ID, awayTeamId)
        }
        outState.putString(KEY_AWAY_TEAM_NAME, awayTeamName)
        outState.putString(KEY_AWAY_TEAM_ABBREVIATION, awayTeamAbbreviation)
        if (::bottomSheetBehavior.isInitialized) outState.putInt(KEY_BOTTOM_SHEET_STATE, bottomSheetBehavior.state)
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
                subtitleTextView.text = getString(R.string.subtitle_last_updated_by_world_rugby, effectiveTime)
                subtitleTextView.isVisible = true
            }
            rankingsViewModel.hasMatchPredictions() -> {
                val matchPredictionCount = rankingsViewModel.getMatchPredictionCount()
                subtitleTextView.text = resources.getQuantityString(R.plurals.subtitle_predicting_matches, matchPredictionCount, matchPredictionCount)
                subtitleTextView.isVisible = true
            }
            else -> {
                subtitleTextView.text = null
                subtitleTextView.isVisible = false
            }
        }
    }

    private fun setupTabsAndViewPager() {
        tabLayout.setupWithViewPager(viewPager)
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = SportFragmentPagerAdapter(childFragmentManager)
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                rankingsViewModel.showMatchPredictionInput.value = position == POSITION_RANKINGS
                if (showMatchPredictionInput) {
                    showBottomSheet()
                    showMatchPredictionInput = false
                }
            }
        })
        // Animate showing/hiding of live tab icon
        ((tabLayout.getChildAt(0) as? LinearLayout)?.getChildAt(POSITION_LIVE) as? LinearLayout)?.layoutTransition = LayoutTransition()
    }

    private fun toggleLiveMatchesTabIcon(show: Boolean) {
        val tab = tabLayout.getTabAt(POSITION_LIVE) ?: return
        if (show) {
            val dotAvd = AnimatedVectorDrawableCompat.create(requireContext(), R.drawable.avd_dot_red_24dp)
            tab.icon = dotAvd
            dotAvd?.start()
        } else {
            if (tab.icon != null) tab.icon = null
        }
    }

    @SuppressWarnings("WrongConstant")
    private fun setupBottomSheet(bottomSheetState: Int?) {
        bottomSheetBehavior = BottomSheetBehavior.from(matchPredictionInputView)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                matchPredictionInputView.updateAlphaForOffset(slideOffset, rankingsViewModel.hasMatchPredictions())
            }
            override fun onStateChanged(bottomSheet: View, state: Int) {
                onBackPressedCallback.isEnabled = state == BottomSheetBehavior.STATE_EXPANDED
                if (state == BottomSheetBehavior.STATE_COLLAPSED || state == BottomSheetBehavior.STATE_HIDDEN) {
                    if (clearMatchPredictionInput) {
                        clearMatchPredictionInput()
                        clearMatchPredictionInput = false
                    }
                    hideSoftInput()
                    matchPredictionInputView.clearMatchPredictionFocus()
                }
            }
        })
        matchPredictionInputView.doOnLayout {
            bottomSheetState?.let { state -> bottomSheetBehavior.state = state }
            val slideOffset = when (bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_EXPANDED -> 1f
                BottomSheetBehavior.STATE_COLLAPSED -> 0f
                else -> -1f
            }
            matchPredictionInputView.updateAlphaForOffset(slideOffset, rankingsViewModel.hasMatchPredictions())
        }
        matchPredictionInputView.listener = object : MatchPredictionInputView.MatchPredictionInputViewListener {

            override fun onHomeTeamClick(position: Int) {
                val worldRugbyRanking = rankingsViewModel.getLatestWorldRugbyRanking(position) ?: return
                if (worldRugbyRanking.teamId == awayTeamId) {
                    val homeTeamText = if (homeTeamAbbreviation == null || homeTeamName == null) null else EmojiCompat.get().process(
                            getString(R.string.menu_item_team, FlagUtils.getFlagEmojiForTeamAbbreviation(homeTeamAbbreviation!!), homeTeamName!!))
                    matchPredictionInputView.homeTeamText = homeTeamText
                    return
                }
                homeTeamId = worldRugbyRanking.teamId
                homeTeamName = worldRugbyRanking.teamName
                homeTeamAbbreviation = worldRugbyRanking.teamAbbreviation
                val homeTeamText = if (homeTeamAbbreviation == null || homeTeamName == null) null else EmojiCompat.get().process(
                        getString(R.string.menu_item_team, FlagUtils.getFlagEmojiForTeamAbbreviation(homeTeamAbbreviation!!), homeTeamName!!))
                matchPredictionInputView.homeTeamText = homeTeamText
            }

            override fun onAwayTeamClick(position: Int) {
                val worldRugbyRanking = rankingsViewModel.getLatestWorldRugbyRanking(position) ?: return
                if (worldRugbyRanking.teamId == homeTeamId) {
                    val awayTeamText = if (awayTeamAbbreviation == null || awayTeamName == null) null else EmojiCompat.get().process(
                            getString(R.string.menu_item_team, FlagUtils.getFlagEmojiForTeamAbbreviation(awayTeamAbbreviation!!), awayTeamName!!))
                    matchPredictionInputView.awayTeamText = awayTeamText
                    return
                }
                awayTeamId = worldRugbyRanking.teamId
                awayTeamName = worldRugbyRanking.teamName
                awayTeamAbbreviation = worldRugbyRanking.teamAbbreviation
                val awayTeamText = if (awayTeamAbbreviation == null || awayTeamName == null) null else EmojiCompat.get().process(
                        getString(R.string.menu_item_team, FlagUtils.getFlagEmojiForTeamAbbreviation(awayTeamAbbreviation!!), awayTeamName!!))
                matchPredictionInputView.awayTeamText = awayTeamText
            }

            override fun onHomeTeamTextChanged(valid: Boolean) {
                rankingsViewModel.homeTeamInputValid.value = valid
            }

            override fun onAwayTeamTextChanged(valid: Boolean) {
                rankingsViewModel.awayTeamInputValid.value = valid
            }

            override fun onHomePointsTextChanged(valid: Boolean) {
                rankingsViewModel.homePointsInputValid.value = valid
            }

            override fun onAwayPointsTextChanged(valid: Boolean) {
                rankingsViewModel.awayPointsInputValid.value = valid
            }

            override fun onAddMatchPredictionClick() {
                clearMatchPredictionInput()
                showBottomSheet()
            }

            override fun onClearOrCancelClick() {
                if (rankingsViewModel.isEditingMatchPrediction()) {
                    hideBottomSheetAndClearMatchPredictionInput()
                } else {
                    clearMatchPredictionInput()
                }
            }

            override fun onCloseClick() {
                hideBottomSheet()
            }

            override fun onAddOrEditMatchPredictionClick() {
                if (addOrEditMatchPredictionFromInput()) {
                    hideBottomSheetAndClearMatchPredictionInput()
                }
            }

            override fun onAwayPointsImeDoneAction(): Boolean {
                if (addOrEditMatchPredictionFromInput()) {
                    hideBottomSheetAndClearMatchPredictionInput()
                    return true
                }
                return false
            }

            override fun onMatchPredictionClick(matchPrediction: MatchPrediction) {
                rankingsViewModel.beginEditMatchPrediction(matchPrediction)
                applyMatchPredictionToInput(matchPrediction)
                showBottomSheet()
            }

            override fun onMatchPredictionRemoveClick(matchPrediction: MatchPrediction) {
                val removedEditingMatchPrediction = rankingsViewModel.removeMatchPrediction(matchPrediction)
                if (removedEditingMatchPrediction) {
                    clearMatchPredictionInput()
                }
            }

            override fun onMatchPredictionsBackgroundClick() {
                showBottomSheet()
            }
        }
    }

    private fun setupAddMatchFab() {
        addMatchPredictionFab.setOnClickListener {
            showBottomSheet()
        }
        TooltipCompat.setTooltipText(addMatchPredictionFab, getString(R.string.tooltip_add_match_prediction))
    }

    private fun setupViewModels() {
        sportViewModel.scrollToTop.observe(viewLifecycleOwner, EventObserver {
            appBarLayout.setExpanded(true)
        })
        rankingsViewModel.worldRugbyRankings.observe(viewLifecycleOwner, Observer { worldRugbyRankings ->
            val isEmpty = worldRugbyRankings?.isEmpty() ?: true
            addMatchPredictionFab.isEnabled = !isEmpty
        })
        rankingsViewModel.latestWorldRugbyRankings.observe(viewLifecycleOwner, Observer { latestWorldRugbyRankings ->
            assignWorldRugbyRankingsToMatchPredictionInputTeams(latestWorldRugbyRankings)
        })
        rankingsViewModel.latestWorldRugbyRankingsEffectiveTime.observe(viewLifecycleOwner, Observer { effectiveTime ->
            setSubtitle(effectiveTime)
        })
        rankingsViewModel.matchPredictions.observe(viewLifecycleOwner, Observer { matchPredictions ->
            matchPredictionInputView.setMatchPredictions(matchPredictions)
        })
        rankingsViewModel.matchPredictionInputValid.observe(viewLifecycleOwner, Observer { matchPredictionInputValid ->
            matchPredictionInputView.setAddOrEditMatchPredictionButtonEnabled(matchPredictionInputValid)
        })
        rankingsViewModel.editingMatchPrediction.observe(viewLifecycleOwner, Observer { editingMatchPrediction ->
            val isEditing = editingMatchPrediction != null
            matchPredictionInputView.adjustForEditing(isEditing)
        })
        rankingsViewModel.matchPredictionInputState.observe(viewLifecycleOwner, Observer { matchPredictionInputState ->
            val showMatchPredictionInput = matchPredictionInputState?.showMatchPredictionInput ?: true
            val hasMatchPredictions = matchPredictionInputState?.hasMatchPredictions ?: false
            matchPredictionInputView.setAddMatchPredictionButtonEnabled(hasMatchPredictions)
            addMatchPredictionFab.apply {
                if (showMatchPredictionInput && !hasMatchPredictions) show() else hide()
            }
            bottomSheetBehavior.apply {
                val isHideable = !showMatchPredictionInput || !hasMatchPredictions
                val skipCollapsed = !showMatchPredictionInput || !hasMatchPredictions
                val state = when {
                    !showMatchPredictionInput || !hasMatchPredictions -> BottomSheetBehavior.STATE_HIDDEN
                    hasMatchPredictions && state == BottomSheetBehavior.STATE_HIDDEN -> BottomSheetBehavior.STATE_COLLAPSED
                    else -> state
                }
                this.isHideable = isHideable
                this.skipCollapsed = skipCollapsed
                this.state = state
            }
        })
        rankingsViewModel.onScroll.observe(viewLifecycleOwner, EventObserver { delta ->
            if (delta > 0) addMatchPredictionFab.shrink() else addMatchPredictionFab.extend()
        })
        liveMatchesViewModel.liveWorldRugbyMatches.observe(viewLifecycleOwner, Observer { liveWorldRugbyMatches ->
            val show = !liveWorldRugbyMatches.isNullOrEmpty()
            toggleLiveMatchesTabIcon(show)
        })
        liveMatchesViewModel.navigatePredict.observe(viewLifecycleOwner, EventObserver { worldRugbyMatch ->
            applyWorldRugbyMatchToInput(worldRugbyMatch)
            showMatchPredictionInput = true
            viewPager.currentItem = POSITION_RANKINGS
            rankingsViewModel.endEditMatchPrediction()
        })
        unplayedMatchesViewModel.navigatePredict.observe(viewLifecycleOwner, EventObserver { worldRugbyMatch ->
            applyWorldRugbyMatchToInput(worldRugbyMatch)
            showMatchPredictionInput = true
            viewPager.currentItem = POSITION_RANKINGS
            rankingsViewModel.endEditMatchPrediction()
        })
    }

    private fun showBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideBottomSheet() {
        if (bottomSheetBehavior.isHideable && bottomSheetBehavior.skipCollapsed) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun assignWorldRugbyRankingsToMatchPredictionInputTeams(worldRugbyRankings: List<WorldRugbyRanking>?) {
        if (worldRugbyRankings == null) return
        val teams = worldRugbyRankings.map { worldRugbyRanking ->
            EmojiCompat.get().process(getString(R.string.menu_item_team,
                    FlagUtils.getFlagEmojiForTeamAbbreviation(worldRugbyRanking.teamAbbreviation), worldRugbyRanking.teamName))
        }
        matchPredictionInputView.setTeams(teams)
    }

    private fun addOrEditMatchPredictionFromInput(): Boolean {
        val homeTeamId = homeTeamId ?: return false
        val homeTeamName = homeTeamName ?: return false
        val homeTeamAbbreviation = homeTeamAbbreviation ?: return false
        val homeTeamScore = matchPredictionInputView.homePointsText
        if (homeTeamScore == MatchPredictionInputView.NO_POINTS) {
            return false
        }
        val awayTeamId = awayTeamId ?: return false
        val awayTeamName = awayTeamName ?: return false
        val awayTeamAbbreviation = awayTeamAbbreviation ?: return false
        val awayTeamScore = matchPredictionInputView.awayPointsText
        if (awayTeamScore == MatchPredictionInputView.NO_POINTS) {
            return false
        }
        val nha = matchPredictionInputView.nhaChecked
        val rwc = matchPredictionInputView.rwcChecked
        val id = when {
            rankingsViewModel.isEditingMatchPrediction() -> rankingsViewModel.editingMatchPrediction.value!!.id
            else -> MatchPrediction.generateId()
        }
        val matchPrediction = MatchPrediction(
                id = id,
                homeTeamId = homeTeamId,
                homeTeamName = homeTeamName,
                homeTeamAbbreviation = homeTeamAbbreviation,
                homeTeamScore = homeTeamScore,
                awayTeamId = awayTeamId,
                awayTeamName = awayTeamName,
                awayTeamAbbreviation = awayTeamAbbreviation,
                awayTeamScore = awayTeamScore,
                noHomeAdvantage = nha,
                rugbyWorldCup = rwc
        )
        return when {
            rankingsViewModel.isEditingMatchPrediction() -> {
                rankingsViewModel.editMatchPrediction(matchPrediction)
                true
            }
            else -> {
                rankingsViewModel.addMatchPrediction(matchPrediction)
                true
            }
        }
    }

    private fun applyMatchPredictionToInput(matchPrediction: MatchPrediction) {
        homeTeamId = matchPrediction.homeTeamId
        homeTeamName = matchPrediction.homeTeamName
        homeTeamAbbreviation = matchPrediction.homeTeamAbbreviation
        val homeTeam = EmojiCompat.get().process(getString(R.string.menu_item_team,
                FlagUtils.getFlagEmojiForTeamAbbreviation(matchPrediction.homeTeamAbbreviation), homeTeamName))
        matchPredictionInputView.homeTeamText = homeTeam
        matchPredictionInputView.homePointsText = matchPrediction.homeTeamScore
        awayTeamId = matchPrediction.awayTeamId
        awayTeamName = matchPrediction.awayTeamName
        awayTeamAbbreviation = matchPrediction.awayTeamAbbreviation
        val awayTeam = EmojiCompat.get().process(getString(R.string.menu_item_team,
                FlagUtils.getFlagEmojiForTeamAbbreviation(matchPrediction.awayTeamAbbreviation), awayTeamName))
        matchPredictionInputView.awayTeamText = awayTeam
        matchPredictionInputView.awayPointsText = matchPrediction.awayTeamScore
        matchPredictionInputView.nhaChecked = matchPrediction.noHomeAdvantage
        matchPredictionInputView.rwcChecked = matchPrediction.rugbyWorldCup
    }

    private fun applyWorldRugbyMatchToInput(worldRugbyMatch: WorldRugbyMatch) {
        homeTeamId = worldRugbyMatch.firstTeamId
        homeTeamName = worldRugbyMatch.firstTeamName
        homeTeamAbbreviation = worldRugbyMatch.firstTeamAbbreviation!!
        val homeTeam = EmojiCompat.get().process(getString(R.string.menu_item_team,
                FlagUtils.getFlagEmojiForTeamAbbreviation(worldRugbyMatch.firstTeamAbbreviation!!), homeTeamName))
        matchPredictionInputView.homeTeamText = homeTeam
        awayTeamId = worldRugbyMatch.secondTeamId
        awayTeamName = worldRugbyMatch.secondTeamName
        awayTeamAbbreviation = worldRugbyMatch.secondTeamAbbreviation!!
        val awayTeam = EmojiCompat.get().process(getString(R.string.menu_item_team,
                FlagUtils.getFlagEmojiForTeamAbbreviation(worldRugbyMatch.secondTeamAbbreviation!!), awayTeamName))
        matchPredictionInputView.awayTeamText = awayTeam
        when (worldRugbyMatch.status) {
            MatchStatus.UNPLAYED -> matchPredictionInputView.clearMatchPredictionPointsInput()
            else -> {
                matchPredictionInputView.homePointsText = worldRugbyMatch.firstTeamScore
                matchPredictionInputView.awayPointsText = worldRugbyMatch.secondTeamScore
            }
        }
        matchPredictionInputView.nhaChecked = worldRugbyMatch.venueCountry?.let { venueCountry ->
            venueCountry != worldRugbyMatch.firstTeamName && venueCountry != worldRugbyMatch.secondTeamName
        } ?: false
        matchPredictionInputView.rwcChecked = worldRugbyMatch.eventLabel?.let { eventLabel ->
            eventLabel.contains("Rugby World Cup", ignoreCase = true) && !eventLabel.contains("Qualifying", ignoreCase = true)
        } ?: false
    }

    private fun hideSoftInput() {
        val imm: InputMethodManager? = requireContext().getSystemService()
        imm?.hideSoftInputFromWindow(requireView().rootView.windowToken, 0)
    }

    private fun hideBottomSheetAndClearMatchPredictionInput() {
        clearMatchPredictionInput = true
        hideBottomSheet()
    }

    private fun clearMatchPredictionInput() {
        homeTeamId = null
        homeTeamName = null
        homeTeamAbbreviation = null
        awayTeamId = null
        awayTeamName = null
        awayTeamAbbreviation = null
        matchPredictionInputView.clearMatchPredictionInput()
        rankingsViewModel.endEditMatchPrediction()
    }

    override fun onDestroyView() {
        hideSoftInput()
        rankingsViewModel.resetMatchPredictionInputValid()
        super.onDestroyView()
    }

    inner class SportFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int) = when (position) {
            POSITION_RANKINGS -> RankingsFragment.newInstance(sport)
            POSITION_LIVE -> LiveMatchesFragment.newInstance(sport)
            POSITION_FIXTURES -> MatchesFragment.newInstance(sport, MatchStatus.UNPLAYED)
            POSITION_RESULTS -> MatchesFragment.newInstance(sport, MatchStatus.COMPLETE)
            else -> throw IllegalArgumentException("Position $position exceeds SportFragmentPagerAdapter count")
        }

        override fun getCount() = 4

        override fun getPageTitle(position: Int) = when (position) {
            POSITION_RANKINGS -> getString(R.string.title_rankings)
            POSITION_LIVE -> getString(R.string.title_live)
            POSITION_FIXTURES -> getString(R.string.title_fixtures)
            POSITION_RESULTS -> getString(R.string.title_results)
            else -> super.getPageTitle(position)
        }
    }

    companion object {
        const val TAG = "SportFragment"
        private const val POSITION_RANKINGS = 0
        private const val POSITION_LIVE = 1
        private const val POSITION_FIXTURES = 2
        private const val POSITION_RESULTS = 3
        private const val KEY_HOME_TEAM_ID = "home_team_id"
        private const val KEY_HOME_TEAM_NAME = "home_team_name"
        private const val KEY_HOME_TEAM_ABBREVIATION = "home_team_abbreviation"
        private const val KEY_AWAY_TEAM_ID = "away_team_id"
        private const val KEY_AWAY_TEAM_NAME = "away_team_name"
        private const val KEY_AWAY_TEAM_ABBREVIATION = "away_team_abbreviation"
        private const val KEY_BOTTOM_SHEET_STATE = "bottom_sheet_state"
    }
}
