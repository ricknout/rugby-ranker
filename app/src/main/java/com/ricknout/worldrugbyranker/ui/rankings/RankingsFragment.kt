package com.ricknout.worldrugbyranker.ui.rankings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.TooltipCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ricknout.worldrugbyranker.R
import com.ricknout.worldrugbyranker.ui.common.MatchResultListAdapter
import com.ricknout.worldrugbyranker.ui.common.OnClickItemTouchListener
import com.ricknout.worldrugbyranker.ui.common.WorldRugbyRankingListAdapter
import com.ricknout.worldrugbyranker.ui.common.OnBackPressedListener
import com.ricknout.worldrugbyranker.ui.common.OnBackPressedProvider
import com.ricknout.worldrugbyranker.vo.MatchResult
import com.ricknout.worldrugbyranker.vo.WorldRugbyRanking
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_rankings.*
import javax.inject.Inject
import android.view.inputmethod.InputMethodManager

class RankingsFragment : DaggerFragment(), OnBackPressedListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: RankingsViewModel

    private val rankingsAdapter = WorldRugbyRankingListAdapter()
    private val matchesAdapter = MatchResultListAdapter()

    private lateinit var homeTeamPopupMenu: PopupMenu
    private lateinit var awayTeamPopupMenu: PopupMenu

    private var homeTeamId: Long? = null
    private var homeTeamAbbreviation: String? = null
    private var awayTeamId: Long? = null
    private var awayTeamAbbreviation: String? = null

    private var type: Int = TYPE_NONE

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var bottomSheetState = BOTTOM_SHEET_STATE_NONE

    private var clearAddMatchInput = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_rankings, container, false)

    @SuppressWarnings("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)
                .get(RankingsViewModel::class.java)
        type = RankingsFragmentArgs.fromBundle(arguments).type
        bottomSheetState = savedInstanceState?.getInt(KEY_BOTTOM_SHEET_STATE, BOTTOM_SHEET_STATE_NONE) ?: BOTTOM_SHEET_STATE_NONE
        homeTeamId = savedInstanceState?.getLong(KEY_HOME_TEAM_ID)
        homeTeamAbbreviation = savedInstanceState?.getString(KEY_HOME_TEAM_ABBREVIATION)
        awayTeamId = savedInstanceState?.getLong(KEY_AWAY_TEAM_ID)
        awayTeamAbbreviation = savedInstanceState?.getString(KEY_AWAY_TEAM_ABBREVIATION)
        rankingsRecyclerView.adapter = rankingsAdapter
        matchesRecyclerView.adapter = matchesAdapter
        homeTeamPopupMenu = PopupMenu(requireContext(), homeTeamEditText).apply {
            setOnMenuItemClickListener { menuItem ->
                val intent = menuItem.intent
                val homeTeamId = intent.extras?.getLong(EXTRA_TEAM_ID)
                val homeTeamAbbreviation = intent.extras?.getString(EXTRA_TEAM_ABBREVIATION)
                if (homeTeamId == awayTeamId) return@setOnMenuItemClickListener true
                this@RankingsFragment.homeTeamId = homeTeamId
                this@RankingsFragment.homeTeamAbbreviation = homeTeamAbbreviation
                homeTeamEditText.setText(menuItem.title)
                true
            }
        }
        awayTeamPopupMenu = PopupMenu(requireContext(), awayTeamEditText).apply {
            setOnMenuItemClickListener { menuItem ->
                val intent = menuItem.intent
                val awayTeamId = intent.extras?.getLong(EXTRA_TEAM_ID)
                val awayTeamAbbreviation = intent.extras?.getString(EXTRA_TEAM_ABBREVIATION)
                if (awayTeamId == homeTeamId) return@setOnMenuItemClickListener true
                this@RankingsFragment.awayTeamId = awayTeamId
                this@RankingsFragment.awayTeamAbbreviation = awayTeamAbbreviation
                awayTeamEditText.setText(menuItem.title)
                true
            }
        }
        homeTeamEditText.apply {
            setOnClickListener {
                homeTeamPopupMenu.show()
            }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val valid = !s.isNullOrEmpty()
                    when (type) {
                        TYPE_MENS -> viewModel.mensHomeTeamInputValid.value = valid
                        TYPE_WOMENS -> viewModel.womensHomeTeamInputValid.value = valid
                    }
                }
                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
        homePointsEditText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val valid = !s.isNullOrEmpty()
                    when (type) {
                        TYPE_MENS -> viewModel.mensHomePointsInputValid.value = valid
                        TYPE_WOMENS -> viewModel.womensHomePointsInputValid.value = valid
                    }
                }
                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
        awayTeamEditText.apply {
            setOnClickListener {
                awayTeamPopupMenu.show()
            }
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val valid = !s.isNullOrEmpty()
                    when (type) {
                        TYPE_MENS -> viewModel.mensAwayTeamInputValid.value = valid
                        TYPE_WOMENS -> viewModel.womensAwayTeamInputValid.value = valid
                    }
                }
                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
        awayPointsEditText.apply {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val valid = !s.isNullOrEmpty()
                    when (type) {
                        TYPE_MENS -> viewModel.mensAwayPointsInputValid.value = valid
                        TYPE_WOMENS -> viewModel.womensAwayPointsInputValid.value = valid
                    }
                }
                override fun afterTextChanged(s: Editable?) {
                }
            })
        }
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                updateAlphaForBottomSheetSlide(slideOffset, isCalculating())
            }
            override fun onStateChanged(bottomSheet: View, state: Int) {
                bottomSheetState = state
                if (state == BottomSheetBehavior.STATE_COLLAPSED || state == BottomSheetBehavior.STATE_HIDDEN) {
                    if (clearAddMatchInput) {
                        clearAddMatchInput()
                        clearAddMatchInput = false
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
        updateAlphaForBottomSheetSlide(slideOffset, isCalculating())
        addMatchFab.setOnClickListener {
            showBottomSheet()
        }
        TooltipCompat.setTooltipText(addMatchFab, getString(R.string.tooltip_add_match))
        matchesRecyclerView.addOnItemTouchListener(OnClickItemTouchListener(requireContext()) {
            showBottomSheet()
        })
        closeButton.setOnClickListener {
            hideBottomSheet()
        }
        addButton.setOnClickListener {
            if (getMatchResultAndCalculate()) {
                hideBottomSheetAndClearAddMatchInput()
            }
        }
        when (type) {
            TYPE_MENS -> {
                titleTextView.setText(R.string.title_mens_rugby_rankings)
                viewModel.mensWorldRugbyRankings.observe(this, Observer { mensWorldRugbyRankings ->
                    rankingsAdapter.submitList(mensWorldRugbyRankings)
                    val isEmpty = mensWorldRugbyRankings?.isEmpty() ?: true
                    addMatchFab.isEnabled = !isEmpty
                })
                viewModel.latestMensWorldRugbyRankings.observe(this, Observer { latestMensWorldRugbyRankings ->
                    assignWorldRugbyRankingsToTeamPopupMenus(latestMensWorldRugbyRankings)
                })
                viewModel.isCalculatingMens.observe(this, Observer { isCalculatingMens ->
                    resetButton.isEnabled = isCalculatingMens
                    resetMatchesButton.isEnabled = isCalculatingMens
                })
                viewModel.mensMatches.observe(this, Observer { mensMatches ->
                    matchesAdapter.submitList(mensMatches)
                    val isEmpty = mensMatches?.isEmpty() ?: true
                    updateUiForMatches(!isEmpty)
                })
                viewModel.mensAddMatchInputValid.observe(this, Observer { mensAddMatchInputValid ->
                    addButton.isEnabled = mensAddMatchInputValid
                })
                resetButton.setOnClickListener {
                    viewModel.resetMens()
                    hideBottomSheetAndClearAddMatchInput()
                }
                resetMatchesButton.setOnClickListener {
                    viewModel.resetMens()
                    hideBottomSheetAndClearAddMatchInput()
                }
            }
            TYPE_WOMENS -> {
                titleTextView.setText(R.string.title_womens_rugby_rankings)
                viewModel.womensWorldRugbyRankings.observe(this, Observer { womensWorldRugbyRankings ->
                    rankingsAdapter.submitList(womensWorldRugbyRankings)
                    val isEmpty = womensWorldRugbyRankings?.isEmpty() ?: true
                    addMatchFab.isEnabled = !isEmpty
                })
                viewModel.latestWomensWorldRugbyRankings.observe(this, Observer { latestWomensWorldRugbyRankings ->
                    assignWorldRugbyRankingsToTeamPopupMenus(latestWomensWorldRugbyRankings)
                })
                viewModel.isCalculatingWomens.observe(this, Observer { isCalculatingWomens ->
                    resetButton.isEnabled = isCalculatingWomens
                    resetMatchesButton.isEnabled = isCalculatingWomens
                })
                viewModel.womensMatches.observe(this, Observer { womensMatches ->
                    matchesAdapter.submitList(womensMatches)
                    val isEmpty = womensMatches?.isEmpty() ?: true
                    updateUiForMatches(!isEmpty)
                })
                viewModel.womensAddMatchInputValid.observe(this, Observer { womensAddMatchInputValid ->
                    addButton.isEnabled = womensAddMatchInputValid
                })
                resetButton.setOnClickListener {
                    viewModel.resetWomens()
                    hideBottomSheetAndClearAddMatchInput()
                }
                resetMatchesButton.setOnClickListener {
                    viewModel.resetWomens()
                    hideBottomSheetAndClearAddMatchInput()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_BOTTOM_SHEET_STATE, bottomSheetState)
        homeTeamId?.let { homeTeamId ->
            outState.putLong(KEY_HOME_TEAM_ID, homeTeamId)
        }
        outState.putString(KEY_HOME_TEAM_ABBREVIATION, homeTeamAbbreviation)
        awayTeamId?.let { awayTeamId ->
            outState.putLong(KEY_AWAY_TEAM_ID, awayTeamId)
        }
        outState.putString(KEY_AWAY_TEAM_ABBREVIATION, awayTeamAbbreviation)
    }

    private fun isCalculating() = when (type) {
        TYPE_MENS -> viewModel.isCalculatingMens()
        TYPE_WOMENS -> viewModel.isCalculatingWomens()
        else -> false
    }

    private fun updateUiForMatches(hasMatches: Boolean) {
        bottomSheetBehavior.isHideable = !hasMatches
        bottomSheetBehavior.skipCollapsed = !hasMatches
        if (!hasMatches && bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        if (hasMatches) addMatchFab.hide() else addMatchFab.show()
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

    private fun updateAlphaForBottomSheetSlide(slideOffset: Float, icCalculating: Boolean) {
        setAlphaAndVisibility(matchesRecyclerView, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_MATCHES))
        setAlphaAndVisibility(resetMatchesButton, if (icCalculating) {
            offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_MATCHES)
        } else {
            0f
        })
        setAlphaAndVisibility(addMatchTitleTextView, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_MATCH))
        setAlphaAndVisibility(addButton, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_MATCH))
        setAlphaAndVisibility(resetButton, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_MATCH))
        setAlphaAndVisibility(closeButton, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_MATCH))
        setAlphaAndVisibility(homeTeamMatchResult, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_MATCH))
        setAlphaAndVisibility(awayTeamMatchResult, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_MATCH))
        setAlphaAndVisibility(nhaCheckBox, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_MATCH))
        setAlphaAndVisibility(rwcCheckBox, offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_MATCH))
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
                putExtra(EXTRA_TEAM_ABBREVIATION, worldRugbyRanking.teamAbbreviation)
            }
            val homeTeamMenuItem = homeTeamPopupMenu.menu.add(worldRugbyRanking.teamName)
            homeTeamMenuItem.intent = intent
            val awayTeamMenuItem = awayTeamPopupMenu.menu.add(worldRugbyRanking.teamName)
            awayTeamMenuItem.intent = intent
        }
    }

    private fun getMatchResultAndCalculate(): Boolean {
        val homeTeamId = homeTeamId ?: return false
        val homeTeamAbbreviation = homeTeamAbbreviation ?: return false
        val homeTeamScore = if (!homePointsEditText.text.isNullOrEmpty()) {
            homePointsEditText.text.toString().toInt()
        } else {
            return false
        }
        val awayTeamId = awayTeamId ?: return false
        val awayTeamAbbreviation = awayTeamAbbreviation ?: return false
        val awayTeamScore = if (!awayPointsEditText.text.isNullOrEmpty()) {
            awayPointsEditText.text.toString().toInt()
        } else {
            return false
        }
        val nha = nhaCheckBox.isChecked
        val rwc = rwcCheckBox.isChecked
        val matchResult = MatchResult(
                homeTeamId,
                homeTeamAbbreviation,
                homeTeamScore,
                awayTeamId,
                awayTeamAbbreviation,
                awayTeamScore,
                nha,
                rwc
        )
        return when (type) {
            TYPE_MENS -> {
                viewModel.calculateMens(matchResult)
                true
            }
            TYPE_WOMENS -> {
                viewModel.calculateWomens(matchResult)
                true
            }
            else -> false
        }
    }

    private fun hideBottomSheetAndClearAddMatchInput() {
        clearAddMatchInput = true
        hideBottomSheet()
    }

    private fun clearAddMatchInput() {
        homeTeamId = null
        homeTeamAbbreviation = null
        awayTeamId = null
        awayTeamAbbreviation = null
        homeTeamEditText.text?.clear()
        homePointsEditText.text?.clear()
        awayTeamEditText.text?.clear()
        awayPointsEditText.text?.clear()
        nhaCheckBox.isChecked = false
        rwcCheckBox.isChecked = false
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (context as? OnBackPressedProvider ?: throw ClassCastException("$context must implement OnBackPressedProvider"))
                .setOnBackPressedListener(this)
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

    private fun hideSoftInput() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
    }

    override fun onDestroyView() {
        hideSoftInput()
        super.onDestroyView()
    }

    companion object {
        const val TAG = "RankingsFragment"
        private const val TYPE_NONE = -1
        private const val TYPE_MENS = 0
        private const val TYPE_WOMENS = 1
        private const val KEY_BOTTOM_SHEET_STATE = "bottom_sheet_state"
        private const val KEY_HOME_TEAM_ID = "home_team_id"
        private const val KEY_HOME_TEAM_ABBREVIATION = "home_team_abbreviation"
        private const val KEY_AWAY_TEAM_ID = "away_team_id"
        private const val KEY_AWAY_TEAM_ABBREVIATION = "away_team_abbreviation"
        private const val EXTRA_TEAM_ID = "team_id"
        private const val EXTRA_TEAM_ABBREVIATION = "team_abbreviation"
        private const val BOTTOM_SHEET_STATE_NONE = -1
        private const val ALPHA_CHANGE_OVER = 0.33f
        private const val ALPHA_MAX_MATCHES = 0f
        private const val ALPHA_MAX_ADD_MATCH = 0.67f
    }
}
