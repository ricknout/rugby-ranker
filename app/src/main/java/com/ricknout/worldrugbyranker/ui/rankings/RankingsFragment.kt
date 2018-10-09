package com.ricknout.worldrugbyranker.ui.rankings

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
import com.ricknout.worldrugbyranker.R
import com.ricknout.worldrugbyranker.vo.MatchResult
import com.ricknout.worldrugbyranker.vo.WorldRugbyRanking
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import android.view.inputmethod.InputMethodManager
import androidx.emoji.text.EmojiCompat
import com.ricknout.worldrugbyranker.ui.common.MatchResultListAdapter
import com.ricknout.worldrugbyranker.ui.common.OnBackgroundClickItemTouchListener
import com.ricknout.worldrugbyranker.ui.common.WorldRugbyRankingListAdapter
import com.ricknout.worldrugbyranker.ui.common.OnBackPressedListener
import com.ricknout.worldrugbyranker.ui.common.OnBackPressedProvider
import com.ricknout.worldrugbyranker.ui.common.SimpleTextWatcher
import com.ricknout.worldrugbyranker.util.FlagUtils
import kotlinx.android.synthetic.main.fragment_rankings.*
import kotlinx.android.synthetic.main.include_add_edit_match_bottom_sheet.*

class RankingsFragment : DaggerFragment(), OnBackPressedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: RankingsViewModel

    private var type: Int = TYPE_NONE

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

    private val rankingsAdapter = WorldRugbyRankingListAdapter()
    private val matchesAdapter = MatchResultListAdapter({ matchResult ->
        when (type) {
            TYPE_MENS -> { viewModel.beginEditMensMatchResult(matchResult) }
            TYPE_WOMENS -> { viewModel.beginEditWomensMatchResult(matchResult) }
        }
        applyMatchResultToInput(matchResult)
        showBottomSheet()
    }, { matchResult ->
        when (type) {
            TYPE_MENS -> {
                val removedMensEditingMatchResult = viewModel.removeMensMatchResult(matchResult)
                if (removedMensEditingMatchResult) {
                    clearAddOrEditMatchInput()
                }
            }
            TYPE_WOMENS -> {
                val removedWomensEditingMatchResult = viewModel.removeWomensMatchResult(matchResult)
                if (removedWomensEditingMatchResult) {
                    clearAddOrEditMatchInput()
                }
            }
        }
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_rankings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)
                .get(RankingsViewModel::class.java)
        type = RankingsFragmentArgs.fromBundle(arguments).type
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
        when (type) {
            TYPE_MENS -> setupMens()
            TYPE_WOMENS -> setupWomens()
        }
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
        rankingsRecyclerView.adapter = rankingsAdapter
        matchesRecyclerView.adapter = matchesAdapter
        matchesRecyclerView.addOnItemTouchListener(OnBackgroundClickItemTouchListener(requireContext()) {
            showBottomSheet()
        })
    }

    @SuppressWarnings("WrongConstant")
    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                updateAlphaForBottomSheetSlide(slideOffset, hasMatches(), isEditingMatch())
            }
            override fun onStateChanged(bottomSheet: View, state: Int) {
                bottomSheetState = state
                if (state == BottomSheetBehavior.STATE_COLLAPSED || state == BottomSheetBehavior.STATE_HIDDEN) {
                    if (clearAddOrEditMatchInput) {
                        clearAddOrEditMatchInput()
                        clearAddOrEditMatchInput = false
                    }
                    hideSoftInput()
                }
            }
        })
        if (bottomSheetState != BOTTOM_SHEET_STATE_NONE) bottomSheetBehavior.state = bottomSheetState
        val slideOffset = when (bottomSheetBehavior.state) {
            BottomSheetBehavior.STATE_EXPANDED -> 1f
            BottomSheetBehavior.STATE_COLLAPSED -> 0f
            else -> -1f
        }
        updateAlphaForBottomSheetSlide(slideOffset, hasMatches(), isEditingMatch())
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
                    when (type) {
                        TYPE_MENS -> viewModel.mensHomeTeamInputValid.value = valid
                        TYPE_WOMENS -> viewModel.womensHomeTeamInputValid.value = valid
                    }
                }
            })
        }
        homePointsEditText.apply {
            addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val valid = !s.isNullOrEmpty()
                    when (type) {
                        TYPE_MENS -> viewModel.mensHomePointsInputValid.value = valid
                        TYPE_WOMENS -> viewModel.womensHomePointsInputValid.value = valid
                    }
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
                    when (type) {
                        TYPE_MENS -> viewModel.mensAwayTeamInputValid.value = valid
                        TYPE_WOMENS -> viewModel.womensAwayTeamInputValid.value = valid
                    }
                }
            })
        }
        awayPointsEditText.apply {
            addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val valid = !s.isNullOrEmpty()
                    when (type) {
                        TYPE_MENS -> viewModel.mensAwayPointsInputValid.value = valid
                        TYPE_WOMENS -> viewModel.womensAwayPointsInputValid.value = valid
                    }
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
        TooltipCompat.setTooltipText(addMatchFab, getString(R.string.tooltip_add_match))
        TooltipCompat.setTooltipText(addMatchButton, getString(R.string.tooltip_add_match))
    }

    private fun setupMens() {
        titleTextView.setText(R.string.title_mens_rugby_rankings)
        viewModel.mensWorldRugbyRankings.observe(this, Observer { mensWorldRugbyRankings ->
            rankingsAdapter.submitList(mensWorldRugbyRankings)
            val isEmpty = mensWorldRugbyRankings?.isEmpty() ?: true
            addMatchFab.isEnabled = !isEmpty
            progressBar.visibility = if (isEmpty) View.VISIBLE else View.GONE
        })
        viewModel.latestMensWorldRugbyRankings.observe(this, Observer { latestMensWorldRugbyRankings ->
            assignWorldRugbyRankingsToTeamPopupMenus(latestMensWorldRugbyRankings)
        })
        viewModel.mensMatches.observe(this, Observer { mensMatches ->
            matchesAdapter.submitList(mensMatches)
            val isEmpty = mensMatches?.isEmpty() ?: true
            updateUiForMatches(!isEmpty)
        })
        viewModel.mensAddOrEditMatchInputValid.observe(this, Observer { mensAddOrEditMatchInputValid ->
            addOrEditButton.isEnabled = mensAddOrEditMatchInputValid
        })
        viewModel.mensEditingMatchResult.observe(this, Observer { mensEditingMatchResult ->
            val isEditing = mensEditingMatchResult != null
            addOrEditMatchTitleTextView.setText(if (isEditing) R.string.title_edit_match else R.string.title_add_match)
            cancelButton.visibility = if (isEditing) View.VISIBLE else View.INVISIBLE
            addOrEditButton.setText(if (isEditing) R.string.button_edit else R.string.button_add)
        })
    }

    private fun setupWomens() {
        titleTextView.setText(R.string.title_womens_rugby_rankings)
        viewModel.womensWorldRugbyRankings.observe(this, Observer { womensWorldRugbyRankings ->
            rankingsAdapter.submitList(womensWorldRugbyRankings)
            val isEmpty = womensWorldRugbyRankings?.isEmpty() ?: true
            addMatchFab.isEnabled = !isEmpty
            progressBar.visibility = if (isEmpty) View.VISIBLE else View.GONE
        })
        viewModel.latestWomensWorldRugbyRankings.observe(this, Observer { latestWomensWorldRugbyRankings ->
            assignWorldRugbyRankingsToTeamPopupMenus(latestWomensWorldRugbyRankings)
        })
        viewModel.womensMatches.observe(this, Observer { womensMatches ->
            matchesAdapter.submitList(womensMatches)
            val isEmpty = womensMatches?.isEmpty() ?: true
            updateUiForMatches(!isEmpty)
        })
        viewModel.womensAddOrEditMatchInputValid.observe(this, Observer { womensAddOrEditMatchInputValid ->
            addOrEditButton.isEnabled = womensAddOrEditMatchInputValid
        })
        viewModel.womensEditingMatchResult.observe(this, Observer { womensEditingMatchResult ->
            val isEditing = womensEditingMatchResult != null
            addOrEditMatchTitleTextView.setText(if (isEditing) R.string.title_edit_match else R.string.title_add_match)
            cancelButton.visibility = if (isEditing) View.VISIBLE else View.INVISIBLE
            addOrEditButton.setText(if (isEditing) R.string.button_edit else R.string.button_add)
        })
    }

    private fun hasMatches() = when (type) {
        TYPE_MENS -> viewModel.hasMensMatches()
        TYPE_WOMENS -> viewModel.hasWomensMatches()
        else -> false
    }

    private fun isEditingMatch() = when (type) {
        TYPE_MENS -> viewModel.isEditingMensMatch()
        TYPE_WOMENS -> viewModel.isEditingWomensMatch()
        else -> false
    }

    private fun updateUiForMatches(hasMatches: Boolean) {
        bottomSheetBehavior.isHideable = !hasMatches
        bottomSheetBehavior.skipCollapsed = !hasMatches
        if (!hasMatches && bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        if (hasMatches) addMatchFab.hide() else addMatchFab.show()
        addMatchButton.isEnabled = hasMatches
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

    private fun updateAlphaForBottomSheetSlide(slideOffset: Float, hasMatches: Boolean, isEditingMatch: Boolean) {
        setAlphaAndVisibility(matchesRecyclerView, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_MATCHES))
        setAlphaAndVisibility(addMatchButton, if (hasMatches) {
            offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_MATCHES)
        } else {
            0f
        })
        setAlphaAndVisibility(addOrEditMatchTitleTextView, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH))
        setAlphaAndVisibility(addOrEditButton, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH))
        setAlphaAndVisibility(cancelButton, if (isEditingMatch) {
            offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH)
        } else {
            0f
        })
        setAlphaAndVisibility(closeButton, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH))
        setAlphaAndVisibility(homeTeamMatchResult, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH))
        setAlphaAndVisibility(awayTeamMatchResult, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH))
        setAlphaAndVisibility(nhaCheckBox, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH))
        setAlphaAndVisibility(rwcCheckBox, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH))
    }

    private fun offsetToAlpha(value: Float, rangeMin: Float, rangeMax: Float): Float {
        return ((value - rangeMin) / (rangeMax - rangeMin)).coerceIn(0f, 1f)
    }

    private fun setAlphaAndVisibility(view: View, alpha: Float) {
        view.alpha = alpha
        view.visibility = if (alpha == 0f) View.INVISIBLE else View.VISIBLE
    }

    private fun assignWorldRugbyRankingsToTeamPopupMenus(worldRugbyRankings: List<WorldRugbyRanking>?) {
        homeTeamPopupMenu.menu.clear()
        awayTeamPopupMenu.menu.clear()
        worldRugbyRankings?.forEach { worldRugbyRanking ->
            val intent = Intent().apply {
                putExtra(EXTRA_TEAM_ID, worldRugbyRanking.teamId)
                putExtra(EXTRA_TEAM_NAME, worldRugbyRanking.teamName)
                putExtra(EXTRA_TEAM_ABBREVIATION, worldRugbyRanking.teamAbbreviation)
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
            isEditingMatch() && type == TYPE_MENS -> viewModel.mensEditingMatchResult.value!!.id
            isEditingMatch() && type == TYPE_WOMENS -> viewModel.womensEditingMatchResult.value!!.id
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
            isEditingMatch() && type == TYPE_MENS -> {
                viewModel.editMensMatchResult(matchResult)
                true
            }
            isEditingMatch() && type == TYPE_WOMENS -> {
                viewModel.editWomensMatchResult(matchResult)
                true
            }
            type == TYPE_MENS -> {
                viewModel.addMensMatchResult(matchResult)
                true
            }
            type == TYPE_WOMENS -> {
                viewModel.addWomensMatchResult(matchResult)
                true
            }
            else -> false
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
        endEditMatchResult()
    }

    private fun endEditMatchResult() {
        when (type) {
            TYPE_MENS -> viewModel.endEditMensMatchResult()
            TYPE_WOMENS -> viewModel.endEditWomensMatchResult()
        }
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
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
    }

    override fun onDestroyView() {
        hideSoftInput()
        when (type) {
            TYPE_MENS -> viewModel.resetMensAddOrEditMatchInputValid()
            TYPE_WOMENS -> viewModel.resetWomensAddOrEditMatchInputValid()
        }
        super.onDestroyView()
    }

    companion object {
        const val TAG = "RankingsFragment"
        private const val TYPE_NONE = -1
        private const val TYPE_MENS = 0
        private const val TYPE_WOMENS = 1
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
        private const val ALPHA_MAX_MATCHES = 0f
        private const val ALPHA_MAX_ADD_OR_EDIT_MATCH = 0.67f
    }
}
