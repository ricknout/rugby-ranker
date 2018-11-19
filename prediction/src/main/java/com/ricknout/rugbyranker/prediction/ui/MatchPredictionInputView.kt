package com.ricknout.rugbyranker.prediction.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.TooltipCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import com.ricknout.rugbyranker.common.ui.BackgroundClickOnItemTouchListener
import com.ricknout.rugbyranker.common.ui.SimpleTextWatcher
import com.ricknout.rugbyranker.prediction.R
import com.ricknout.rugbyranker.prediction.vo.MatchPrediction
import kotlinx.android.synthetic.main.view_match_prediction_input.view.*

class MatchPredictionInputView
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    interface MatchPredictionInputViewListener {
        fun onHomeTeamClick()
        fun onAwayTeamClick()
        fun onHomeTeamTextChanged(valid: Boolean)
        fun onAwayTeamTextChanged(valid: Boolean)
        fun onHomePointsTextChanged(valid: Boolean)
        fun onAwayPointsTextChanged(valid: Boolean)
        fun onAddMatchPredictionClick()
        fun onClearOrCancelClick()
        fun onCloseClick()
        fun onAddOrEditMatchPredictionClick()
        fun onAwayPointsImeDoneAction(): Boolean
        fun onMatchPredictionClick(matchPrediction: MatchPrediction)
        fun onMatchPredictionRemoveClick(matchPrediction: MatchPrediction)
        fun onMatchPredictionsBackgroundClick()
    }

    var listener: MatchPredictionInputViewListener? = null

    var homeTeamText: CharSequence?
        get() = homeTeamEditText.text?.toString()
        set(value) = homeTeamEditText.setText(value)

    var awayTeamText: CharSequence?
        get() = awayTeamEditText.text?.toString()
        set(value) = awayTeamEditText.setText(value)

    var homePointsText: Int
        get() = if (homePointsEditText.text.isNullOrEmpty()) NO_POINTS else homePointsEditText.text.toString().toInt()
        set(value) = homePointsEditText.setText(value.toString())

    var awayPointsText: Int
        get() = if (awayPointsEditText.text.isNullOrEmpty()) NO_POINTS else awayPointsEditText.text.toString().toInt()
        set(value) = awayPointsEditText.setText(value.toString())

    var rwcChecked: Boolean
        get() = rwcCheckBox.isChecked
        set(value) { rwcCheckBox.isChecked = value }

    var nhaChecked: Boolean
        get() = nhaCheckBox.isChecked
        set(value) { nhaCheckBox.isChecked = value }

    private val matchPredictionAdapter = MatchPredictionListAdapter({ matchPrediction ->
        listener?.onMatchPredictionClick(matchPrediction)
    }, { matchPrediction ->
        listener?.onMatchPredictionRemoveClick(matchPrediction)
    })

    init {
        inflate(context, R.layout.view_match_prediction_input, this)
        setupMatchPredictionRecyclerView()
        setupEditTexts()
        setupAddMatchPredictionButton()
        setupClearOrCancelButton()
        setupCloseButton()
        setupAddOrEditMatchPredictionButton()
    }

    private fun setupMatchPredictionRecyclerView() {
        matchPredictionsRecyclerView.adapter = matchPredictionAdapter
        matchPredictionsRecyclerView.addOnItemTouchListener(BackgroundClickOnItemTouchListener(context) {
            listener?.onMatchPredictionsBackgroundClick()
        })
    }

    private fun setupEditTexts() {
        homeTeamEditText.apply {
            setOnClickListener {
                listener?.onHomeTeamClick()
            }
            addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val valid = !s.isNullOrEmpty()
                    listener?.onHomeTeamTextChanged(valid)
                }
            })
        }
        awayTeamEditText.apply {
            setOnClickListener {
                listener?.onAwayTeamClick()
            }
            addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val valid = !s.isNullOrEmpty()
                    listener?.onAwayTeamTextChanged(valid)
                }
            })
        }
        homePointsEditText.apply {
            addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val valid = !s.isNullOrEmpty()
                    listener?.onHomePointsTextChanged(valid)
                }
            })
        }
        awayPointsEditText.apply {
            addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val valid = !s.isNullOrEmpty()
                    listener?.onAwayPointsTextChanged(valid)
                }
            })
        }
        awayPointsEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                return@setOnEditorActionListener listener?.onAwayPointsImeDoneAction() ?: false
            }
            false
        }
    }

    private fun setupAddMatchPredictionButton() {
        addMatchPredictionButton.setOnClickListener {
            listener?.onAddMatchPredictionClick()
        }
        TooltipCompat.setTooltipText(addMatchPredictionButton, context.getString(R.string.tooltip_add_match_prediction))
    }

    private fun setupClearOrCancelButton() {
        clearOrCancelButton.setOnClickListener {
            listener?.onClearOrCancelClick()
        }
    }

    private fun setupCloseButton() {
        closeButton.setOnClickListener {
            listener?.onCloseClick()
        }
    }

    private fun setupAddOrEditMatchPredictionButton() {
        addOrEditMatchPredictionButton.setOnClickListener {
            listener?.onAddOrEditMatchPredictionClick()
        }
    }

    fun setMatchPredictions(matchPredictions: List<MatchPrediction>?) {
        matchPredictionAdapter.submitList(matchPredictions)
    }

    fun clearMatchPredictionFocus() {
        homePointsEditText.clearFocus()
        awayPointsEditText.clearFocus()
    }

    fun clearMatchPredictionInput() {
        homeTeamEditText.text?.clear()
        homePointsEditText.text?.clear()
        awayTeamEditText.text?.clear()
        awayPointsEditText.text?.clear()
        nhaCheckBox.isChecked = false
        rwcCheckBox.isChecked = false
    }

    fun clearMatchPredictionPointsInput() {
        homePointsEditText.text?.clear()
        awayPointsEditText.text?.clear()
    }

    fun getHomeTeamAnchorView(): View = homeTeamEditText

    fun getAwayTeamAnchorView(): View = awayTeamEditText

    fun adjustForEditing(isEditing: Boolean) {
        matchPredictionTitleTextView.setText(if (isEditing) R.string.title_edit_match_prediction else R.string.title_add_match_prediction)
        clearOrCancelButton.setText(if (isEditing) R.string.button_cancel else R.string.button_clear)
        addOrEditMatchPredictionButton.setText(if (isEditing) R.string.button_edit else R.string.button_add)
    }

    fun setAddMatchPredictionButtonEnabled(enabled: Boolean) {
        addMatchPredictionButton.isEnabled = enabled
    }

    fun setAddOrEditMatchPredictionButtonEnabled(enabled: Boolean) {
        addOrEditMatchPredictionButton.isEnabled = enabled
    }

    fun updateAlphaForOffset(offset: Float, hasMatchPredictions: Boolean) {
        setAlphaAndVisibility(matchPredictionsRecyclerView, offsetToAlpha(offset, ALPHA_CHANGE_OVER, ALPHA_MAX_MATCH_PREDICTIONS))
        setAlphaAndVisibility(addMatchPredictionButton, if (hasMatchPredictions) {
            offsetToAlpha(offset, ALPHA_CHANGE_OVER, ALPHA_MAX_MATCH_PREDICTIONS)
        } else {
            0f
        })
        setAlphaAndVisibility(matchPredictionTitleTextView, offsetToAlpha(offset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH_PREDICTION))
        setAlphaAndVisibility(clearOrCancelButton, offsetToAlpha(offset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH_PREDICTION))
        setAlphaAndVisibility(closeButton, offsetToAlpha(offset, ALPHA_CHANGE_OVER, ALPHA_MAX_ADD_OR_EDIT_MATCH_PREDICTION))
    }

    private fun offsetToAlpha(value: Float, rangeMin: Float, rangeMax: Float): Float {
        return ((value - rangeMin) / (rangeMax - rangeMin)).coerceIn(0f, 1f)
    }

    private fun setAlphaAndVisibility(view: View, alpha: Float) {
        view.alpha = alpha
        view.isInvisible = alpha == 0f
    }

    companion object {
        const val TAG = "MatchPredictionInputView"
        const val NO_POINTS = -1
        private const val ALPHA_CHANGE_OVER = 0.33f
        private const val ALPHA_MAX_MATCH_PREDICTIONS = 0f
        private const val ALPHA_MAX_ADD_OR_EDIT_MATCH_PREDICTION = 0.67f
    }
}
