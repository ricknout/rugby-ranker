package dev.ricknout.rugbyranker.prediction.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.ricknout.rugbyranker.prediction.databinding.ListItemPredictionBinding
import dev.ricknout.rugbyranker.prediction.model.Prediction

class PredictionAdapter(
    private val onClick: (prediction: Prediction) -> Unit,
    private val onCloseIconClick: (prediction: Prediction) -> Unit,
) : ListAdapter<Prediction, PredictionViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PredictionViewHolder(
        ListItemPredictionBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    )

    override fun onBindViewHolder(holder: PredictionViewHolder, position: Int) {
        val prediction = getItem(position)
        holder.bind(prediction, onClick, onCloseIconClick)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Prediction>() {
            override fun areItemsTheSame(oldItem: Prediction, newItem: Prediction) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Prediction, newItem: Prediction) = oldItem == newItem
        }
    }
}
