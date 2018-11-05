package com.ricknout.rugbyranker.ui.rankings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.TooltipCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ricknout.rugbyranker.R
import com.ricknout.rugbyranker.vo.MatchResult
import com.ricknout.rugbyranker.vo.WorldRugbyRanking
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.emoji.text.EmojiCompat
import com.ricknout.rugbyranker.ui.common.MatchResultListAdapter
import com.ricknout.rugbyranker.common.ui.BackgroundClickOnItemTouchListener
import com.ricknout.rugbyranker.ui.common.WorldRugbyRankingListAdapter
import com.ricknout.rugbyranker.common.ui.OnBackPressedListener
import com.ricknout.rugbyranker.common.ui.OnBackPressedProvider
import com.ricknout.rugbyranker.common.ui.SimpleTextWatcher
import com.ricknout.rugbyranker.util.FlagUtils
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.work.State
import com.google.android.material.snackbar.Snackbar
import com.ricknout.rugbyranker.vo.Sport
import kotlinx.android.synthetic.main.fragment_rankings.*
import kotlinx.android.synthetic.main.include_add_edit_match_bottom_sheet.*

class RankingsFragment : DaggerFragment(), OnBackPressedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: RankingsViewModel

    private lateinit var sport: Sport

    private var homeTeamId: Long? = null
    private var homeTeamName: String? = null
    private var homeTeamAbbreviation: String? = null
    private var awayTeamId: Long? = null
    private var awayTeamName: String? = null
    private var awayTeamAbbreviation: String? = null

    private var clearAddOrEditMatchInput = false

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var bottomSheetState = BOTTOM_SHEET_STATE_NONE

    private lateinit var homeTeamPopupMenu: PopupMenu
    private lateinit var awayTeamPopupMenu: PopupMenu

    private lateinit var workerSnackBar: Snackbar
    private lateinit var refreshSnackBar: Snackbar

    private val worldRugbyRankingAdapter = WorldRugbyRankingListAdapter()
    private val matchResultAdapter = MatchResultListAdapter({ matchResult ->
        viewModel.beginEditMatchResult(matchResult)
        applyMatchResultToInput(matchResult)
        showBottomSheet()
    }, { matchResult ->
        val removedEditingMatchResult = viewModel.removeMatchResult(matchResult)
        if (removedEditingMatchResult) {
            clearAddOrEditMatchInput()
        }
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_rankings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sportOrdinal = RankingsFragmentArgs.fromBundle(arguments).sportOrdinal
        sport = Sport.values()[sportOrdinal]
        viewModel = when (sport) {
            Sport.MENS -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                    .get(MensRankingsViewModel::class.java)
            Sport.WOMENS -> ViewModelProviders.of(requireActivity(), viewModelFactory)
                    .get(WomensRankingsViewModel::class.java)
        }
        homeTeamId = savedInstanceState?.getLong(KEY_HOME_TEAM_ID)
        homeTeamName = savedInstanceState?.getString(KEY_HOME_TEAM_NAME)
        homeTeamAbbreviation = savedInstanceState?.getString(KEY_HOME_TEAM_ABBREVIATION)
        awayTeamId = savedInstanceState?.getLong(KEY_AWAY_TEAM_ID)
        awayTeamName = savedInstanceState?.getString(KEY_AWAY_TEAM_NAME)
        awayTeamAbbreviation = savedInstanceState?.getString(KEY_AWAY_TEAM_ABBREVIATION)
        bottomSheetState = savedInstanceState?.getInt(KEY_BOTTOM_SHEET_STATE, BOTTOM_SHEET_STATE_NONE) ?: BOTTOM_SHEET_STATE_NONE
        setupRecyclerViews()
        setupBottomSheet()
        setupAddOrEditMatchInput()
        setupAddMatchButtons()
        setupSnackbars()
        setupViewModel()
        setupSwipeRefreshLayout()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_BOTTOM_SHEET_STATE, bottomSheetState)
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
    }

    private fun setupRecyclerViews() {
        rankingsRecyclerView.adapter = worldRugbyRankingAdapter
        matchesRecyclerView.adapter = matchResultAdapter
        matchesRecyclerView.addOnItemTouchListener(BackgroundClickOnItemTouchListener(requireContext()) {
            showBottomSheet()
        })
    }

    @SuppressWarnings("WrongConstant")
    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                updateAlphaForBottomSheetSlide(slideOffset, hasMatchResults(), isEditingMatchResult())
            }
            override fun onStateChanged(bottomSheet: View, state: Int) {
                bottomSheetState = state
                if (state == BottomSheetBehavior.STATE_COLLAPSED || state == BottomSheetBehavior.STATE_HIDDEN) {
                    if (clearAddOrEditMatchInput) {
                        clearAddOrEditMatchInput()
                        clearAddOrEditMatchInput = false
                    }
                    hideSoftInput()
                    homePointsEditText.clearFocus()
                    awayPointsEditText.clearFocus()
                }
            }
        })
        if (bottomSheetState != BOTTOM_SHEET_STATE_NONE) bottomSheetBehavior.state = bottomSheetState
        val slideOffset = when (bottomSheetBehavior.state) {
            BottomSheetBehavior.STATE_EXPANDED -> 1f
            BottomSheetBehavior.STATE_COLLAPSED -> 0f
            else -> -1f
        }
        updateAlphaForBottomSheetSlide(slideOffset, hasMatchResults(), isEditingMatchResult())
    }

    private fun setupAddOrEditMatchInput() {
        homeTeamPopupMenu = PopupMenu(requireContext(), homeTeamEditText).apply {
            setOnMenuItemClickListener { menuItem ->
                val intent = menuItem.intent
                val homeTeamId = intent.extras?.getLong(EXTRA_TEAM_ID)
                val homeTeamName = intent.extras?.getString(EXTRA_TEAM_NAME)
                val homeTeamAbbreviation = intent.extras?.getString(EXTRA_TEAM_ABBREVIATION)
                if (homeTeamId == awayTeamId) return@setOnMenuItemClickListener true
                this@RankingsFragment.homeTeamId = homeTeamId
                this@RankingsFragment.homeTeamName = homeTeamName
                this@RankingsFragment.homeTeamAbbreviation = homeTeamAbbreviation
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
                    viewModel.homeTeamInputValid.value = valid
                }
            })
        }
        homePointsEditText.apply {
            addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val valid = !s.isNullOrEmpty()
                    viewModel.homePointsInputValid.value = valid
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
                this@RankingsFragment.awayTeamId = awayTeamId
                this@RankingsFragment.awayTeamName = awayTeamName
                this@RankingsFragment.awayTeamAbbreviation = awayTeamAbbreviation
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
                    viewModel.awayTeamInputValid.value = valid
                }
            })
        }
        awayPointsEditText.apply {
            addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val valid = !s.isNullOrEmpty()
                    viewModel.awayPointsInputValid.value = valid
                }
            })
        }
        cancelButton.setOnClickListener {
            hideBottomSheetAndClearAddOrEditMatchInput()
        }
        closeButton.setOnClickListener {
            hideBottomSheet()
        }
        addOrEditButton.setOnClickListener {
            if (addOrEditMatchResultFromInput()) {
                hideBottomSheetAndClearAddOrEditMatchInput()
            }
        }
        awayPointsEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (addOrEditMatchResultFromInput()) {
                    hideBottomSheetAndClearAddOrEditMatchInput()
                    return@setOnEditorActionListener true
                }
            }
            false
        }
    }

    private fun setupAddMatchButtons() {
        addMatchFab.setOnClickListener {
            showBottomSheet()
        }
        addMatchButton.setOnClickListener {
            clearAddOrEditMatchInput()
            showBottomSheet()
        }
        TooltipCompat.setTooltipText(addMatchFab, getString(R.string.tooltip_add_match_prediction))
        TooltipCompat.setTooltipText(addMatchButton, getString(R.string.tooltip_add_match_prediction))
    }

    private fun setupSnackbars() {
        workerSnackBar = Snackbar.make(root, "", Snackbar.LENGTH_INDEFINITE)
        refreshSnackBar = Snackbar.make(root, "", Snackbar.LENGTH_SHORT)
    }

    private fun setupViewModel() {
        viewModel.worldRugbyRankings.observe(this, Observer { worldRugbyRankings ->
            worldRugbyRankingAdapter.submitList(worldRugbyRankings)
            val isEmpty = worldRugbyRankings?.isEmpty() ?: true
            addMatchFab.isEnabled = !isEmpty
            progressBar.isVisible = isEmpty
        })
        viewModel.latestWorldRugbyRankings.observe(this, Observer { latestWorldRugbyRankings ->
            assignWorldRugbyRankingsToTeamPopupMenus(latestWorldRugbyRankings)
        })
        viewModel.latestWorldRugbyRankingsStatuses.observe(this, Observer { workStatuses ->
            val workStatus = if (workStatuses != null && !workStatuses.isEmpty()) workStatuses[0] else return@Observer
            when (workStatus.state) {
                State.RUNNING -> {
                    swipeRefreshLayout.isEnabled = false
                    workerSnackBar.setText(R.string.snackbar_fetching_world_rugby_rankings)
                    workerSnackBar.show()
                }
                else -> {
                    swipeRefreshLayout.isEnabled = true
                    root.post { workerSnackBar.dismiss() }
                }
            }
        })
        viewModel.latestWorldRugbyRankingsEffectiveTime.observe(this, Observer { effectiveTime ->
            setSubtitle(effectiveTime)
        })
        viewModel.refreshingLatestWorldRugbyRankings.observe(this, Observer { refreshingLatestWorldRugbyRankings ->
            swipeRefreshLayout.isRefreshing = refreshingLatestWorldRugbyRankings
        })
        viewModel.matchResults.observe(this, Observer { matchResults ->
            matchResultAdapter.submitList(matchResults)
            val isEmpty = matchResults?.isEmpty() ?: true
            updateUiForMatchResults(!isEmpty)
            setTitle(!isEmpty)
        })
        viewModel.addOrEditMatchInputValid.observe(this, Observer { addOrEditMatchInputValid ->
            addOrEditButton.isEnabled = addOrEditMatchInputValid
        })
        viewModel.editingMatchResult.observe(this, Observer { editingMatchResult ->
            val isEditing = editingMatchResult != null
            addOrEditMatchTitleTextView.setText(if (isEditing) R.string.title_edit_match_prediction else R.string.title_add_match_prediction)
            cancelButton.isInvisible = !isEditing
            addOrEditButton.setText(if (isEditing) R.string.button_edit else R.string.button_add)
        })
    }

    private fun setupSwipeRefreshLayout() {
        val swipeRefreshColors = resources.getIntArray(R.array.colors_swipe_refresh)
        swipeRefreshLayout.setColorSchemeColors(*swipeRefreshColors)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshLatestWorldRugbyRankings { success ->
                if (!success) {
                    refreshSnackBar.setText(R.string.snackbar_failed_to_refresh_world_rugby_rankings)
                    refreshSnackBar.show()
                }
            }
        }
    }

    private fun hasMatchResults() = viewModel.hasMatchResults()

    private fun isEditingMatchResult() = viewModel.isEditingMatchResult()

    private fun updateUiForMatchResults(hasMatchResults: Boolean) {
        bottomSheetBehavior.isHideable = !hasMatchResults
        bottomSheetBehavior.skipCollapsed = !hasMatchResults
        if (!hasMatchResults && bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        if (hasMatchResults) addMatchFab.hide() else addMatchFab.show()
        addMatchButton.isEnabled = hasMatchResults
    }

    private fun setTitle(hasMatchResults: Boolean) {
        titleTextView.setText(when (sport) {
            Sport.MENS -> {
                if (hasMatchResults) {
                    R.string.title_predicted_mens_rugby_rankings
                } else {
                    R.string.title_latest_mens_rugby_rankings
                }
            }
            Sport.WOMENS -> {
                if (hasMatchResults) {
                    R.string.title_predicted_womens_rugby_rankings
                } else {
                    R.string.title_latest_womens_rugby_rankings
                }
            }
        })
    }

    private fun setSubtitle(effectiveTime: String?) {
        subtitleTextView.text = if (effectiveTime == null) {
            null
        } else {
            getString(R.string.subtitle_last_updated, effectiveTime)
        }
        subtitleTextView.isVisible = effectiveTime != null
    }

    private fun showBottomSheet() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun hideBottomSheet() {
        bottomSheetBehavior.state = if (bottomSheetBehavior.skipCollapsed) {
            BottomSheetBehavior.STATE_HIDDEN
        } else {
            BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun updateAlphaForBottomSheetSlide(slideOffset: Float, hasMatchResults: Boolean, isEditingMatchResult: Boolean) {
        setAlphaAndVisibility(matchesRecyclerView, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_MATCH_RESULTS))
        setAlphaAndVisibility(addMatchButton, if (hasMatchResults) {
            offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_MATCH_RESULTS)
        } else {
            0f
        })
        setAlphaAndVisibility(addOrEditMatchTitleTextView, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH))
        setAlphaAndVisibility(cancelButton, if (isEditingMatchResult) {
            offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH)
        } else {
            0f
        })
        setAlphaAndVisibility(closeButton, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH))
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

    private fun addOrEditMatchResultFromInput(): Boolean {
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
            isEditingMatchResult() -> viewModel.editingMatchResult.value!!.id
            else -> MatchResult.generateId()
        }
        val matchResult = MatchResult(
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
            isEditingMatchResult() -> {
                viewModel.editMatchResult(matchResult)
                true
            }
            else -> {
                viewModel.addMatchResult(matchResult)
                true
            }
        }
    }

    private fun applyMatchResultToInput(matchResult: MatchResult) {
        homeTeamId = matchResult.homeTeamId
        homeTeamName = matchResult.homeTeamName
        homeTeamAbbreviation = matchResult.homeTeamAbbreviation
        val homeTeam = EmojiCompat.get().process(getString(R.string.menu_item_team,
                FlagUtils.getFlagEmojiForTeamAbbreviation(matchResult.homeTeamAbbreviation), homeTeamName))
        homeTeamEditText.setText(homeTeam)
        homePointsEditText.setText(matchResult.homeTeamScore.toString())
        awayTeamId = matchResult.awayTeamId
        awayTeamName = matchResult.awayTeamName
        awayTeamAbbreviation = matchResult.awayTeamAbbreviation
        val awayTeam = EmojiCompat.get().process(getString(R.string.menu_item_team,
                FlagUtils.getFlagEmojiForTeamAbbreviation(matchResult.awayTeamAbbreviation), awayTeamName))
        awayTeamEditText.setText(awayTeam)
        awayPointsEditText.setText(matchResult.awayTeamScore.toString())
        nhaCheckBox.isChecked = matchResult.noHomeAdvantage
        rwcCheckBox.isChecked = matchResult.rugbyWorldCup
    }

    private fun hideBottomSheetAndClearAddOrEditMatchInput() {
        clearAddOrEditMatchInput = true
        hideBottomSheet()
    }

    private fun clearAddOrEditMatchInput() {
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
        viewModel.endEditMatchResult()
    }

    override fun onBackPressed(): Boolean {
        if (::bottomSheetBehavior.isInitialized && bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            if (bottomSheetBehavior.isHideable && bottomSheetBehavior.skipCollapsed) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            return true
        }
        return super.onBackPressed()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (context as? OnBackPressedProvider ?: throw ClassCastException("$context must implement OnBackPressedProvider"))
                .setOnBackPressedListener(this)
    }

    private fun hideSoftInput() {
        val imm: InputMethodManager? = requireContext().getSystemService()
        imm?.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
    }

    override fun onDestroyView() {
        hideSoftInput()
        viewModel.resetAddOrEditMatchInputValid()
        super.onDestroyView()
    }

    companion object {
        const val TAG = "RankingsFragment"
        private const val KEY_BOTTOM_SHEET_STATE = "bottom_sheet_state"
        private const val KEY_HOME_TEAM_ID = "home_team_id"
        private const val KEY_HOME_TEAM_NAME = "home_team_name"
        private const val KEY_HOME_TEAM_ABBREVIATION = "home_team_abbreviation"
        private const val KEY_AWAY_TEAM_ID = "away_team_id"
        private const val KEY_AWAY_TEAM_NAME = "away_team_name"
        private const val KEY_AWAY_TEAM_ABBREVIATION = "away_team_abbreviation"
        private const val EXTRA_TEAM_ID = "team_id"
        private const val EXTRA_TEAM_NAME = "team_name"
        private const val EXTRA_TEAM_ABBREVIATION = "team_abbreviation"
        private const val BOTTOM_SHEET_STATE_NONE = -1
        private const val ALPHA_CHANGE_OVER = 0.33f
        private const val ALPHA_MAX_MATCH_RESULTS = 0f
        private const val ALPHA_MAX_ADD_OR_EDIT_MATCH = 0.67f
    }
}
