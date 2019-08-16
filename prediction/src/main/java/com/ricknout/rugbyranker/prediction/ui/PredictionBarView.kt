package com.ricknout.rugbyranker.prediction.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.appcompat.widget.TooltipCompat
import com.ricknout.rugbyranker.prediction.R
import com.ricknout.rugbyranker.prediction.vo.Prediction
import kotlinx.android.synthetic.main.view_prediction_bar.view.*

class PredictionBarView
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr) {

    interface PredictionBarViewListener {
        fun onAddPredictionClick()
        fun onPredictionClick(prediction: Prediction)
        fun onPredictionRemoveClick(prediction: Prediction)
    }

    var listener: PredictionBarViewListener? = null

    private val predictionAdapter = PredictionListAdapter({ prediction ->
        listener?.onPredictionClick(prediction)
    }, { prediction ->
        listener?.onPredictionRemoveClick(prediction)
    })

    init {
        inflate(context, R.layout.view_prediction_bar, this)
        setupPredictionsRecyclerView()
        setupAddPredictionButton()
    }

    private fun setupPredictionsRecyclerView() {
        predictionsRecyclerView.adapter = predictionAdapter
    }

    private fun setupAddPredictionButton() {
        addPredictionButton.setOnClickListener {
            listener?.onAddPredictionClick()
        }
        TooltipCompat.setTooltipText(addPredictionButton, context.getString(R.string.tooltip_add_match_prediction))
    }

    fun setPredictions(predictions: List<Prediction>?) {
        predictionAdapter.submitList(predictions)
    }
}
