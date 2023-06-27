package dev.ricknout.rugbyranker.prediction.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.res.getColorStateListOrThrow
import androidx.core.content.res.getDimensionOrThrow
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.withStyledAttributes
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.ShapeAppearanceModel
import dev.ricknout.rugbyranker.prediction.R
import dev.ricknout.rugbyranker.prediction.databinding.ViewPredictionBarBinding
import dev.ricknout.rugbyranker.prediction.model.Prediction

class PredictionBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.predictionBarStyle,
) : MaterialCardView(context, attrs, defStyleAttr) {

    interface PredictionBarListener {
        fun onAddPredictionClick()
        fun onPredictionClick(prediction: Prediction)
        fun onRemovePredictionClick(prediction: Prediction)
    }

    var listener: PredictionBarListener? = null

    private val adapter = PredictionAdapter(
        { prediction ->
            listener?.onPredictionClick(prediction)
        },
        { prediction ->
            listener?.onRemovePredictionClick(prediction)
        },
    )

    private var _binding: ViewPredictionBarBinding? = null
    private val binding get() = _binding!!

    init {
        _binding = ViewPredictionBarBinding.inflate(LayoutInflater.from(context), this)
        context.withStyledAttributes(attrs, R.styleable.PredictionBar, defStyleAttr, R.style.Widget_RugbyRanker_PredictionBar) {
            val shapeAppearanceResId = getResourceIdOrThrow(R.styleable.PredictionBar_shapeAppearance)
            val shapeAppearanceOverlayResId = getResourceIdOrThrow(R.styleable.PredictionBar_shapeAppearanceOverlay)
            shapeAppearanceModel = ShapeAppearanceModel.builder(
                context,
                shapeAppearanceResId,
                shapeAppearanceOverlayResId,
            ).build()
            setCardBackgroundColor(getColorStateListOrThrow(R.styleable.PredictionBar_backgroundTint))
            cardElevation = getDimensionOrThrow(R.styleable.PredictionBar_elevation)
            binding.addPrediction.apply {
                iconTint = getColorStateListOrThrow(R.styleable.PredictionBar_iconTint)
                rippleColor = getColorStateListOrThrow(R.styleable.PredictionBar_iconRippleColor)
            }
        }
        setupRecyclerView()
        setupButton()
    }

    override fun onDetachedFromWindow() {
        _binding = null
        super.onDetachedFromWindow()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
    }

    private fun setupButton() {
        binding.addPrediction.setOnClickListener {
            listener?.onAddPredictionClick()
        }
        TooltipCompat.setTooltipText(binding.addPrediction, context.getString(R.string.add_prediction))
    }

    fun setPredictions(predictions: List<Prediction>?) {
        adapter.submitList(predictions)
    }

    fun getPredictions(): List<Prediction> = adapter.currentList
}
