package com.ricknout.rugbyranker.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.doOnLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.emoji.text.EmojiCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ricknout.rugbyranker.R
import com.ricknout.rugbyranker.common.livedata.EventObserver
import com.ricknout.rugbyranker.common.ui.BackgroundClickOnItemTouchListener
import com.ricknout.rugbyranker.common.ui.SimpleTextWatcher
import com.ricknout.rugbyranker.ui.common.MatchPredictionListAdapter
import com.ricknout.rugbyranker.ui.matches.MatchesFragment
import com.ricknout.rugbyranker.ui.matches.MatchesViewModel
import com.ricknout.rugbyranker.ui.matches.MensUnplayedMatchesViewModel
import com.ricknout.rugbyranker.ui.matches.WomensUnplayedMatchesViewModel
import com.ricknout.rugbyranker.ui.rankings.RankingsViewModel
import com.ricknout.rugbyranker.ui.rankings.MensRankingsViewModel
import com.ricknout.rugbyranker.ui.rankings.WomensRankingsViewModel
import com.ricknout.rugbyranker.ui.rankings.RankingsFragment
import com.ricknout.rugbyranker.util.FlagUtils
import com.ricknout.rugbyranker.vo.MatchStatus
import com.ricknout.rugbyranker.vo.Sport
import com.ricknout.rugbyranker.vo.MatchPrediction
import com.ricknout.rugbyranker.vo.WorldRugbyRanking
import com.ricknout.rugbyranker.vo.WorldRugbyMatch
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_sport.*
import kotlinx.android.synthetic.main.include_match_prediction_bottom_sheet.*
import java.lang.IllegalArgumentException
import javax.inject.Inject

class SportFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var rankingsViewModel: RankingsViewModel
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

    private val matchPredictionAdapter = MatchPredictionListAdapter({ matchPrediction ->
        rankingsViewModel.beginEditMatchPrediction(matchPrediction)
        applyMatchPredictionToInput(matchPrediction)
        showBottomSheet()
    }, { matchPrediction ->
        val removedEditingMatchPrediction = rankingsViewModel.removeMatchPrediction(matchPrediction)
        if (removedEditingMatchPrediction) {
            clearMatchPredictionInput()
        }
    })

    private val onBackPressedCallback = OnBackPressedCallback {
        if (::bottomSheetBehavior.isInitialized && bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            hideBottomSheet()
            return@OnBackPressedCallback true
        }
        false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_sport, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sportOrdinal = SportFragmentArgs.fromBundle(arguments).sportOrdinal
        sport = Sport.values()[sportOrdinal]
        rankingsViewModel = when (sport) {
            Sport.MENS -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                    .get(MensRankingsViewModel::class.java)
            Sport.WOMENS -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                    .get(WomensRankingsViewModel::class.java)
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
        viewPager.offscreenPageLimit = 2
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

    @SuppressWarnings("WrongConstant")
    private fun setupBottomSheet(bottomSheetState: Int?) {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                updateAlphaForBottomSheetSlide(slideOffset, rankingsViewModel.hasMatchPredictions(), rankingsViewModel.isEditingMatchPrediction())
            }
            override fun onStateChanged(bottomSheet: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_COLLAPSED || state == BottomSheetBehavior.STATE_HIDDEN) {
                    if (clearMatchPredictionInput) {
                        clearMatchPredictionInput()
                        clearMatchPredictionInput = false
                    }
                    hideSoftInput()
                    homePointsEditText.clearFocus()
                    awayPointsEditText.clearFocus()
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
            updateAlphaForBottomSheetSlide(slideOffset, rankingsViewModel.hasMatchPredictions(), rankingsViewModel.isEditingMatchPrediction())
        }
        matchePredictionsRecyclerView.adapter = matchPredictionAdapter
        matchePredictionsRecyclerView.addOnItemTouchListener(BackgroundClickOnItemTouchListener(requireContext()) {
            showBottomSheet()
        })
        homeTeamPopupMenu = PopupMenu(requireContext(), homeTeamEditText).apply {
            setOnMenuItemClickListener { menuItem ->
                val intent = menuItem.intent
                val homeTeamId = intent.extras?.getLong(EXTRA_TEAM_ID)
                val homeTeamName = intent.extras?.getString(EXTRA_TEAM_NAME)
                val homeTeamAbbreviation = intent.extras?.getString(EXTRA_TEAM_ABBREVIATION)
                if (homeTeamId == awayTeamId) return@setOnMenuItemClickListener true
                this@SportFragment.homeTeamId = homeTeamId
                this@SportFragment.homeTeamName = homeTeamName
                this@SportFragment.homeTeamAbbreviation = homeTeamAbbreviation
                homeTeamEditText.setText(menuItem.title)
                true
            }
        }
        homeTeamEditText.apply {
            setOnClickListener {
                homeTeamPopupMenu.show()
            }
            addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val valid = !s.isNullOrEmpty()
                    rankingsViewModel.homeTeamInputValid.value = valid
                }
            })
        }
        homePointsEditText.apply {
            addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val valid = !s.isNullOrEmpty()
                    rankingsViewModel.homePointsInputValid.value = valid
                }
            })
        }
        awayTeamPopupMenu = PopupMenu(requireContext(), awayTeamEditText).apply {
            setOnMenuItemClickListener { menuItem ->
                val intent = menuItem.intent
                val awayTeamId = intent.extras?.getLong(EXTRA_TEAM_ID)
                val awayTeamName = intent.extras?.getString(EXTRA_TEAM_NAME)
                val awayTeamAbbreviation = intent.extras?.getString(EXTRA_TEAM_ABBREVIATION)
                if (awayTeamId == homeTeamId) return@setOnMenuItemClickListener true
                this@SportFragment.awayTeamId = awayTeamId
                this@SportFragment.awayTeamName = awayTeamName
                this@SportFragment.awayTeamAbbreviation = awayTeamAbbreviation
                awayTeamEditText.setText(menuItem.title)
                true
            }
        }
        awayTeamEditText.apply {
            setOnClickListener {
                awayTeamPopupMenu.show()
            }
            addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val valid = !s.isNullOrEmpty()
                    rankingsViewModel.awayTeamInputValid.value = valid
                }
            })
        }
        awayPointsEditText.apply {
            addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val valid = !s.isNullOrEmpty()
                    rankingsViewModel.awayPointsInputValid.value = valid
                }
            })
        }
        cancelButton.setOnClickListener {
            hideBottomSheetAndClearMatchPredictionInput()
        }
        closeButton.setOnClickListener {
            hideBottomSheet()
        }
        addOrEditMatchPredictionButton.setOnClickListener {
            if (addOrEditMatchPredictionFromInput()) {
                hideBottomSheetAndClearMatchPredictionInput()
            }
        }
        awayPointsEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (addOrEditMatchPredictionFromInput()) {
                    hideBottomSheetAndClearMatchPredictionInput()
                    return@setOnEditorActionListener true
                }
            }
            false
        }
        addMatchPredictionButton.setOnClickListener {
            clearMatchPredictionInput()
            showBottomSheet()
        }
        TooltipCompat.setTooltipText(addMatchPredictionButton, getString(R.string.tooltip_add_match_prediction))
    }

    private fun setupAddMatchFab() {
        addMatchPredictionFab.setOnClickListener {
            showBottomSheet()
        }
        TooltipCompat.setTooltipText(addMatchPredictionFab, getString(R.string.tooltip_add_match_prediction))
    }

    private fun setupViewModels() {
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
            matchPredictionAdapter.submitList(matchPredictions)
        })
        rankingsViewModel.matchPredictionInputValid.observe(viewLifecycleOwner, Observer { matchPredictionInputValid ->
            addOrEditMatchPredictionButton.isEnabled = matchPredictionInputValid
        })
        rankingsViewModel.editingMatchPrediction.observe(viewLifecycleOwner, Observer { editingMatchPrediction ->
            val isEditing = editingMatchPrediction != null
            matchPredictionTitleTextView.setText(if (isEditing) R.string.title_edit_match_prediction else R.string.title_add_match_prediction)
            cancelButton.isInvisible = !isEditing
            addOrEditMatchPredictionButton.setText(if (isEditing) R.string.button_edit else R.string.button_add)
        })
        rankingsViewModel.matchPredictionInputState.observe(viewLifecycleOwner, Observer { matchPredictionInputState ->
            val showMatchPredictionInput = matchPredictionInputState?.showMatchPredictionInput ?: true
            val hasMatchPredictions = matchPredictionInputState?.hasMatchPredictions ?: false
            addMatchPredictionButton.isEnabled = hasMatchPredictions
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
        unplayedMatchesViewModel.navigatePredict.observe(viewLifecycleOwner, EventObserver { worldRugbyMatch ->
            applyWorldRugbyMatchToInput(worldRugbyMatch)
            showMatchPredictionInput = true
            viewPager.currentItem = POSITION_RANKINGS
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

    private fun updateAlphaForBottomSheetSlide(slideOffset: Float, hasMatchPredictions: Boolean, isEditingMatchPrediction: Boolean) {
        setAlphaAndVisibility(matchePredictionsRecyclerView, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_MATCH_PREDICTIONS))
        setAlphaAndVisibility(addMatchPredictionButton, if (hasMatchPredictions) {
            offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_MATCH_PREDICTIONS)
        } else {
            0f
        })
        setAlphaAndVisibility(matchPredictionTitleTextView, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH_PREDICTION))
        setAlphaAndVisibility(cancelButton, if (isEditingMatchPrediction) {
            offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH_PREDICTION)
        } else {
            0f
        })
        setAlphaAndVisibility(closeButton, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH_PREDICTION))
    }

    private fun offsetToAlpha(value: Float, rangeMin: Float, rangeMax: Float): Float {
        return ((value - rangeMin) / (rangeMax - rangeMin)).coerceIn(0f, 1f)
    }

    private fun setAlphaAndVisibility(view: View, alpha: Float) {
        view.alpha = alpha
        view.isInvisible = alpha == 0f
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
        val homeTeamScore = if (!homePointsEditText.text.isNullOrEmpty()) {
            homePointsEditText.text.toString().toInt()
        } else {
            return false
        }
        val awayTeamId = awayTeamId ?: return false
        val awayTeamName = awayTeamName ?: return false
        val awayTeamAbbreviation = awayTeamAbbreviation ?: return false
        val awayTeamScore = if (!awayPointsEditText.text.isNullOrEmpty()) {
            awayPointsEditText.text.toString().toInt()
        } else {
            return false
        }
        val nha = nhaCheckBox.isChecked
        val rwc = rwcCheckBox.isChecked
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
        homeTeamEditText.setText(homeTeam)
        homePointsEditText.setText(matchPrediction.homeTeamScore.toString())
        awayTeamId = matchPrediction.awayTeamId
        awayTeamName = matchPrediction.awayTeamName
        awayTeamAbbreviation = matchPrediction.awayTeamAbbreviation
        val awayTeam = EmojiCompat.get().process(getString(R.string.menu_item_team,
                FlagUtils.getFlagEmojiForTeamAbbreviation(matchPrediction.awayTeamAbbreviation), awayTeamName))
        awayTeamEditText.setText(awayTeam)
        awayPointsEditText.setText(matchPrediction.awayTeamScore.toString())
        nhaCheckBox.isChecked = matchPrediction.noHomeAdvantage
        rwcCheckBox.isChecked = matchPrediction.rugbyWorldCup
    }

    private fun applyWorldRugbyMatchToInput(worldRugbyMatch: WorldRugbyMatch) {
        homeTeamId = worldRugbyMatch.firstTeamId
        homeTeamName = worldRugbyMatch.firstTeamName
        homeTeamAbbreviation = worldRugbyMatch.firstTeamAbbreviation!!
        val homeTeam = EmojiCompat.get().process(getString(R.string.menu_item_team,
                FlagUtils.getFlagEmojiForTeamAbbreviation(worldRugbyMatch.firstTeamAbbreviation), homeTeamName))
        homeTeamEditText.setText(homeTeam)
        homePointsEditText.text?.clear()
        awayTeamId = worldRugbyMatch.secondTeamId
        awayTeamName = worldRugbyMatch.secondTeamName
        awayTeamAbbreviation = worldRugbyMatch.secondTeamAbbreviation!!
        val awayTeam = EmojiCompat.get().process(getString(R.string.menu_item_team,
                FlagUtils.getFlagEmojiForTeamAbbreviation(worldRugbyMatch.secondTeamAbbreviation), awayTeamName))
        awayTeamEditText.setText(awayTeam)
        awayPointsEditText.text?.clear()
        nhaCheckBox.isChecked = worldRugbyMatch.venueCountry?.let { venueCountry ->
            venueCountry != worldRugbyMatch.firstTeamName && venueCountry != worldRugbyMatch.secondTeamName
        } ?: false
        rwcCheckBox.isChecked = worldRugbyMatch.eventLabel?.let { eventLabel ->
            eventLabel.contains("Rugby World Cup", ignoreCase = true) && !eventLabel.contains("Qualifying", ignoreCase = true)
        } ?: false
    }

    private fun hideSoftInput() {
        val imm: InputMethodManager? = requireContext().getSystemService()
        imm?.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
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
        homeTeamEditText.text?.clear()
        homePointsEditText.text?.clear()
        awayTeamEditText.text?.clear()
        awayPointsEditText.text?.clear()
        nhaCheckBox.isChecked = false
        rwcCheckBox.isChecked = false
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
            POSITION_FIXTURES -> MatchesFragment.newInstance(sport, MatchStatus.UNPLAYED)
            POSITION_RESULTS -> MatchesFragment.newInstance(sport, MatchStatus.COMPLETE)
            else -> throw IllegalArgumentException("Position $position exceeds SportFragmentPagerAdapter count")
        }

        override fun getCount() = 3

        override fun getPageTitle(position: Int) = when (position) {
            POSITION_RANKINGS -> getString(R.string.title_rankings)
            POSITION_FIXTURES -> getString(R.string.title_fixtures)
            POSITION_RESULTS -> getString(R.string.title_results)
            else -> super.getPageTitle(position)
        }
    }

    companion object {
        const val TAG = "SportFragment"
        private const val POSITION_RANKINGS = 0
        private const val POSITION_FIXTURES = 1
        private const val POSITION_RESULTS = 2
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
        private const val ALPHA_CHANGE_OVER = 0.33f
        private const val ALPHA_MAX_MATCH_PREDICTIONS = 0f
        private const val ALPHA_MAX_ADD_OR_EDIT_MATCH_PREDICTION = 0.67f
    }
}
