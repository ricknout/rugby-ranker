package com.ricknout.worldrugbyranker.ui.rankings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ricknout.worldrugbyranker.R
import com.ricknout.worldrugbyranker.ui.common.MatchResultListAdapter
import com.ricknout.worldrugbyranker.ui.common.WorldRugbyRankingListAdapter
import com.ricknout.worldrugbyranker.vo.MatchResult
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_rankings.*
import java.util.Random
import javax.inject.Inject

class RankingsFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: RankingsViewModel

    private val rankingsAdapter = WorldRugbyRankingListAdapter()
    private val matchesAdapter = MatchResultListAdapter()

    private var type: Int = TYPE_NONE

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var bottomSheetState = BOTTOM_SHEET_STATE_NONE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.fragment_rankings, container, false)

    @SuppressWarnings("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rankingsRecyclerView.adapter = rankingsAdapter
        matchesRecyclerView.adapter = matchesAdapter
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                updateAlphaForBottomSheetSlide(slideOffset)
            }
            override fun onStateChanged(bottomSheet: View, state: Int) {
                bottomSheetState = state
            }
        })
        bottomSheetState = savedInstanceState?.getInt(KEY_BOTTOM_SHEET_STATE, BOTTOM_SHEET_STATE_NONE) ?: BOTTOM_SHEET_STATE_NONE
        if (bottomSheetState != BOTTOM_SHEET_STATE_NONE) bottomSheetBehavior.state = bottomSheetState
        addMatchFab.setOnClickListener {
            showBottomSheet()
        }
        TooltipCompat.setTooltipText(addMatchFab, getString(R.string.tooltip_add_match))
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)
                .get(RankingsViewModel::class.java)
        type = RankingsFragmentArgs.fromBundle(arguments).type
        when (type) {
            TYPE_MENS -> {
                titleTextView.setText(R.string.title_mens_rankings)
                viewModel.mensWorldRugbyRankings.observe(this, Observer { mensWorldRugbyRankings ->
                    rankingsAdapter.submitList(mensWorldRugbyRankings)
                    val isEmpty = mensWorldRugbyRankings?.isEmpty() ?: true
                    calculateButton.isEnabled = !isEmpty
                })
                viewModel.isCalculatingMens.observe(this, Observer { isCalculatingMens ->
                    resetButton.isEnabled = isCalculatingMens
                })
                viewModel.mensMatches.observe(this, Observer { mensMatches ->
                    matchesAdapter.submitList(mensMatches)
                    val isEmpty = mensMatches?.isEmpty() ?: true
                    updateUiForMatches(!isEmpty)
                })
                // Testing calculate
                calculateButton.setOnClickListener {
                    val matchResult = MatchResult(
                            homeTeamId = 37,
                            homeTeamAbbreviation = "NZL",
                            homeTeamScore = Random().nextInt(100),
                            awayTeamId = 39,
                            awayTeamAbbreviation = "RSA",
                            awayTeamScore = Random().nextInt(100),
                            noHomeAdvantage = false,
                            rugbyWorldCup = false
                    )
                    viewModel.calculateMens(matchResult)
                    hideBottomSheet()
                }
                resetButton.setOnClickListener {
                    viewModel.resetMens()
                    hideBottomSheet()
                }
            }
            TYPE_WOMENS -> {
                titleTextView.setText(R.string.title_womens_rankings)
                viewModel.womensWorldRugbyRankings.observe(this, Observer { womensWorldRugbyRankings ->
                    rankingsAdapter.submitList(womensWorldRugbyRankings)
                    val isEmpty = womensWorldRugbyRankings?.isEmpty() ?: true
                    calculateButton.isEnabled = !isEmpty
                })
                viewModel.isCalculatingWomens.observe(this, Observer { isCalculatingWomens ->
                    resetButton.isEnabled = isCalculatingWomens
                })
                viewModel.womensMatches.observe(this, Observer { womensMatches ->
                    matchesAdapter.submitList(womensMatches)
                    val isEmpty = womensMatches?.isEmpty() ?: true
                    updateUiForMatches(!isEmpty)
                })
                // Testing calculate
                calculateButton.setOnClickListener {
                    val matchResult = MatchResult(
                            homeTeamId = 2580,
                            homeTeamAbbreviation = "NZL",
                            homeTeamScore = Random().nextInt(100),
                            awayTeamId = 2582,
                            awayTeamAbbreviation = "RSA",
                            awayTeamScore = Random().nextInt(100),
                            noHomeAdvantage = false,
                            rugbyWorldCup = false
                    )
                    viewModel.calculateWomens(matchResult)
                    hideBottomSheet()
                }
                resetButton.setOnClickListener {
                    viewModel.resetWomens()
                    hideBottomSheet()
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_BOTTOM_SHEET_STATE, bottomSheetState)
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

    private fun updateAlphaForBottomSheetSlide(slideOffset: Float) {
        matchesRecyclerView.alpha = offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_MATCHES)
        calculateButton.alpha = offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_CALCULATE)
        resetButton.alpha = offsetToAlpha(slideOffset, ALPHA_CHANGE_OVER, ALPHA_MAX_CALCULATE)
    }

    private fun offsetToAlpha(value: Float, rangeMin: Float, rangeMax: Float): Float {
        return ((value - rangeMin) / (rangeMax - rangeMin)).coerceIn(0f, 1f)
    }

    companion object {
        const val TAG = "RankingsFragment"
        private const val TYPE_NONE = -1
        private const val TYPE_MENS = 0
        private const val TYPE_WOMENS = 1
        private const val KEY_BOTTOM_SHEET_STATE = "bottom_sheet_state"
        private const val BOTTOM_SHEET_STATE_NONE = -1
        private const val ALPHA_CHANGE_OVER = 0.33f
        private const val ALPHA_MAX_MATCHES = 0f
        private const val ALPHA_MAX_CALCULATE = 0.67f
    }
}