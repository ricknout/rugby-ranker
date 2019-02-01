package com.ricknout.rugbyranker.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.emoji.text.EmojiCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ricknout.rugbyranker.R
import com.ricknout.rugbyranker.common.livedata.EventObserver
import com.ricknout.rugbyranker.matches.ui.MatchesFragment
import com.ricknout.rugbyranker.matches.ui.MatchesViewModel
import com.ricknout.rugbyranker.matches.ui.MensUnplayedMatchesViewModel
import com.ricknout.rugbyranker.matches.ui.WomensUnplayedMatchesViewModel
import com.ricknout.rugbyranker.rankings.ui.RankingsViewModel
import com.ricknout.rugbyranker.rankings.ui.MensRankingsViewModel
import com.ricknout.rugbyranker.rankings.ui.WomensRankingsViewModel
import com.ricknout.rugbyranker.rankings.ui.RankingsFragment
import com.ricknout.rugbyranker.common.util.FlagUtils
import com.ricknout.rugbyranker.matches.vo.MatchStatus
import com.ricknout.rugbyranker.common.vo.Sport
import com.ricknout.rugbyranker.live.ui.LiveMatchesFragment
import com.ricknout.rugbyranker.live.ui.LiveMatchesViewModel
import com.ricknout.rugbyranker.live.ui.MensLiveMatchesViewModel
import com.ricknout.rugbyranker.live.ui.WomensLiveMatchesViewModel
import com.ricknout.rugbyranker.prediction.ui.MatchPredictionInputView
import com.ricknout.rugbyranker.prediction.vo.MatchPrediction
import com.ricknout.rugbyranker.rankings.vo.WorldRugbyRanking
import com.ricknout.rugbyranker.matches.vo.WorldRugbyMatch
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_sport.*
import java.lang.IllegalArgumentException
import javax.inject.Inject

class SportFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var sportViewModel: SportViewModel
    private lateinit var rankingsViewModel: RankingsViewModel
    private lateinit var liveMatchesViewModel: LiveMatchesViewModel
    private lateinit var unplayedMatchesViewModel: MatchesViewModel

    private lateinit var sport: Sport

    private var homeTeamId: Long? = null
    private var homeTeamName: String? = null
    private var homeTeamAbbreviation: String? = null
    private var awayTeamId: Long? = null
    private var awayTeamName: String? = null
    private var awayTeamAbbreviation: String? = null

    private var clearMatchPredictionInput = false
    private var showMatchPredictionInput = false

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private lateinit var homeTeamPopupMenu: PopupMenu
    private lateinit var awayTeamPopupMenu: PopupMenu

    private val onBackPressedCallback = OnBackPressedCallback {
        if (::bottomSheetBehavior.isInitialized && bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            hideBottomSheet()
            return@OnBackPressedCallback true
        }
        false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_sport, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sport = SportFragmentArgs.fromBundle(arguments).sport
        sportViewModel = when (sport) {
            Sport.MENS -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                    .get(MensViewModel::class.java)
            Sport.WOMENS -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                    .get(WomensViewModel::class.java)
        }
        rankingsViewModel = when (sport) {
            Sport.MENS -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                    .get(MensRankingsViewModel::class.java)
            Sport.WOMENS -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                    .get(WomensRankingsViewModel::class.java)
        }
        liveMatchesViewModel = when (sport) {
            Sport.MENS -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                    .get(MensLiveMatchesViewModel::class.java)
            Sport.WOMENS -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                    .get(WomensLiveMatchesViewModel::class.java)
        }
        unplayedMatchesViewModel = when (sport) {
            Sport.MENS -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                    .get(MensUnplayedMatchesViewModel::class.java)
            Sport.WOMENS -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                    .get(WomensUnplayedMatchesViewModel::class.java)
        }
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
        requireActivity().addOnBackPressedCallback(onBackPressedCallback)
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
        outState.putInt(KEY_BOTTOM_SHEET_STATE, bottomSheetBehavior.state)
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
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                this@SportFragment.bottomSheet.updateAlphaForOffset(slideOffset, rankingsViewModel.hasMatchPredictions())
            }
            override fun onStateChanged(bottomSheet: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_COLLAPSED || state == BottomSheetBehavior.STATE_HIDDEN) {
                    if (clearMatchPredictionInput) {
                        clearMatchPredictionInput()
                        clearMatchPredictionInput = false
                    }
                    hideSoftInput()
                    this@SportFragment.bottomSheet.clearMatchPredictionFocus()
                }
            }
        })
        bottomSheet.doOnLayout {
            bottomSheetState?.let { state -> bottomSheetBehavior.state = state }
            val slideOffset = when (bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_EXPANDED -> 1f
                BottomSheetBehavior.STATE_COLLAPSED -> 0f
                else -> -1f
            }
            this@SportFragment.bottomSheet.updateAlphaForOffset(slideOffset, rankingsViewModel.hasMatchPredictions())
        }
        homeTeamPopupMenu = PopupMenu(requireContext(), bottomSheet.getHomeTeamAnchorView()).apply {
            setOnMenuItemClickListener { menuItem ->
                val intent = menuItem.intent
                val homeTeamId = intent.extras?.getLong(EXTRA_TEAM_ID)
                val homeTeamName = intent.extras?.getString(EXTRA_TEAM_NAME)
                val homeTeamAbbreviation = intent.extras?.getString(EXTRA_TEAM_ABBREVIATION)
                if (homeTeamId == awayTeamId) return@setOnMenuItemClickListener true
                this@SportFragment.homeTeamId = homeTeamId
                this@SportFragment.homeTeamName = homeTeamName
                this@SportFragment.homeTeamAbbreviation = homeTeamAbbreviation
                bottomSheet.homeTeamText = menuItem.title
                true
            }
        }
        awayTeamPopupMenu = PopupMenu(requireContext(), bottomSheet.getAwayTeamAnchorView()).apply {
            setOnMenuItemClickListener { menuItem ->
                val intent = menuItem.intent
                val awayTeamId = intent.extras?.getLong(EXTRA_TEAM_ID)
                val awayTeamName = intent.extras?.getString(EXTRA_TEAM_NAME)
                val awayTeamAbbreviation = intent.extras?.getString(EXTRA_TEAM_ABBREVIATION)
                if (awayTeamId == homeTeamId) return@setOnMenuItemClickListener true
                this@SportFragment.awayTeamId = awayTeamId
                this@SportFragment.awayTeamName = awayTeamName
                this@SportFragment.awayTeamAbbreviation = awayTeamAbbreviation
                bottomSheet.awayTeamText = menuItem.title
                true
            }
        }
        bottomSheet.listener = object : MatchPredictionInputView.MatchPredictionInputViewListener {

            override fun onHomeTeamClick() {
                homeTeamPopupMenu.show()
            }

            override fun onAwayTeamClick() {
                awayTeamPopupMenu.show()
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
        sportViewModel.navigateReselect.observe(viewLifecycleOwner, EventObserver {
            appBarLayout.setExpanded(true)
        })
        rankingsViewModel.worldRugbyRankings.observe(viewLifecycleOwner, Observer { worldRugbyRankings ->
            val isEmpty = worldRugbyRankings?.isEmpty() ?: true
            addMatchPredictionFab.isEnabled = !isEmpty
        })
        rankingsViewModel.latestWorldRugbyRankings.observe(viewLifecycleOwner, Observer { latestWorldRugbyRankings ->
            assignWorldRugbyRankingsToTeamPopupMenus(latestWorldRugbyRankings)
        })
        rankingsViewModel.latestWorldRugbyRankingsEffectiveTime.observe(viewLifecycleOwner, Observer { effectiveTime ->
            setSubtitle(effectiveTime)
        })
        rankingsViewModel.matchPredictions.observe(viewLifecycleOwner, Observer { matchPredictions ->
            bottomSheet.setMatchPredictions(matchPredictions)
        })
        rankingsViewModel.matchPredictionInputValid.observe(viewLifecycleOwner, Observer { matchPredictionInputValid ->
            bottomSheet.setAddOrEditMatchPredictionButtonEnabled(matchPredictionInputValid)
        })
        rankingsViewModel.editingMatchPrediction.observe(viewLifecycleOwner, Observer { editingMatchPrediction ->
            val isEditing = editingMatchPrediction != null
            bottomSheet.adjustForEditing(isEditing)
        })
        rankingsViewModel.matchPredictionInputState.observe(viewLifecycleOwner, Observer { matchPredictionInputState ->
            val showMatchPredictionInput = matchPredictionInputState?.showMatchPredictionInput ?: true
            val hasMatchPredictions = matchPredictionInputState?.hasMatchPredictions ?: false
            bottomSheet.setAddMatchPredictionButtonEnabled(hasMatchPredictions)
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

    private fun assignWorldRugbyRankingsToTeamPopupMenus(worldRugbyRankings: List<WorldRugbyRanking>?) {
        homeTeamPopupMenu.menu.clear()
        awayTeamPopupMenu.menu.clear()
        worldRugbyRankings?.forEach { worldRugbyRanking ->
            val intent = Intent().apply {
                replaceExtras(bundleOf(
                        EXTRA_TEAM_ID to worldRugbyRanking.teamId,
                        EXTRA_TEAM_NAME to worldRugbyRanking.teamName,
                        EXTRA_TEAM_ABBREVIATION to worldRugbyRanking.teamAbbreviation
                ))
            }
            val team = EmojiCompat.get().process(getString(R.string.menu_item_team,
                    FlagUtils.getFlagEmojiForTeamAbbreviation(worldRugbyRanking.teamAbbreviation), worldRugbyRanking.teamName))
            val homeTeamMenuItem = homeTeamPopupMenu.menu.add(team)
            homeTeamMenuItem.intent = intent
            val awayTeamMenuItem = awayTeamPopupMenu.menu.add(team)
            awayTeamMenuItem.intent = intent
        }
    }

    private fun addOrEditMatchPredictionFromInput(): Boolean {
        val homeTeamId = homeTeamId ?: return false
        val homeTeamName = homeTeamName ?: return false
        val homeTeamAbbreviation = homeTeamAbbreviation ?: return false
        val homeTeamScore = bottomSheet.homePointsText
        if (homeTeamScore == MatchPredictionInputView.NO_POINTS) {
            return false
        }
        val awayTeamId = awayTeamId ?: return false
        val awayTeamName = awayTeamName ?: return false
        val awayTeamAbbreviation = awayTeamAbbreviation ?: return false
        val awayTeamScore = bottomSheet.awayPointsText
        if (awayTeamScore == MatchPredictionInputView.NO_POINTS) {
            return false
        }
        val nha = bottomSheet.nhaChecked
        val rwc = bottomSheet.rwcChecked
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
        bottomSheet.homeTeamText = homeTeam
        bottomSheet.homePointsText = matchPrediction.homeTeamScore
        awayTeamId = matchPrediction.awayTeamId
        awayTeamName = matchPrediction.awayTeamName
        awayTeamAbbreviation = matchPrediction.awayTeamAbbreviation
        val awayTeam = EmojiCompat.get().process(getString(R.string.menu_item_team,
                FlagUtils.getFlagEmojiForTeamAbbreviation(matchPrediction.awayTeamAbbreviation), awayTeamName))
        bottomSheet.awayTeamText = awayTeam
        bottomSheet.awayPointsText = matchPrediction.awayTeamScore
        bottomSheet.nhaChecked = matchPrediction.noHomeAdvantage
        bottomSheet.rwcChecked = matchPrediction.rugbyWorldCup
    }

    private fun applyWorldRugbyMatchToInput(worldRugbyMatch: WorldRugbyMatch) {
        homeTeamId = worldRugbyMatch.firstTeamId
        homeTeamName = worldRugbyMatch.firstTeamName
        homeTeamAbbreviation = worldRugbyMatch.firstTeamAbbreviation!!
        val homeTeam = EmojiCompat.get().process(getString(R.string.menu_item_team,
                FlagUtils.getFlagEmojiForTeamAbbreviation(worldRugbyMatch.firstTeamAbbreviation!!), homeTeamName))
        bottomSheet.homeTeamText = homeTeam
        awayTeamId = worldRugbyMatch.secondTeamId
        awayTeamName = worldRugbyMatch.secondTeamName
        awayTeamAbbreviation = worldRugbyMatch.secondTeamAbbreviation!!
        val awayTeam = EmojiCompat.get().process(getString(R.string.menu_item_team,
                FlagUtils.getFlagEmojiForTeamAbbreviation(worldRugbyMatch.secondTeamAbbreviation!!), awayTeamName))
        bottomSheet.awayTeamText = awayTeam
        bottomSheet.clearMatchPredictionPointsInput()
        bottomSheet.nhaChecked = worldRugbyMatch.venueCountry?.let { venueCountry ->
            venueCountry != worldRugbyMatch.firstTeamName && venueCountry != worldRugbyMatch.secondTeamName
        } ?: false
        bottomSheet.rwcChecked = worldRugbyMatch.eventLabel?.let { eventLabel ->
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
        bottomSheet.clearMatchPredictionInput()
        rankingsViewModel.endEditMatchPrediction()
    }

    override fun onDestroyView() {
        hideSoftInput()
        rankingsViewModel.resetMatchPredictionInputValid()
        requireActivity().removeOnBackPressedCallback(onBackPressedCallback)
        super.onDestroyView()
    }

    inner class SportFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

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
        private const val EXTRA_TEAM_ID = "team_id"
        private const val EXTRA_TEAM_NAME = "team_name"
        private const val EXTRA_TEAM_ABBREVIATION = "team_abbreviation"
    }
}
