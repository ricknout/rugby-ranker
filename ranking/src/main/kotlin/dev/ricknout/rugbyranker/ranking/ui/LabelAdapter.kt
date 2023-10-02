package dev.ricknout.rugbyranker.ranking.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.ricknout.rugbyranker.ranking.databinding.ListItemLabelBinding

class LabelAdapter : ListAdapter<String, LabelViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = LabelViewHolder(
        ListItemLabelBinding.inflate(LayoutInflater.from(parent.context), parent, false),
    )

    override fun onBindViewHolder(
        holder: LabelViewHolder,
        position: Int,
    ) {
        val label = getItem(position)
        holder.bind(label)
    }

    companion object {
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(
                    oldItem: String,
                    newItem: String,
                ) = oldItem == newItem

                override fun areContentsTheSame(
                    oldItem: String,
                    newItem: String,
                ) = oldItem == newItem
            }
    }
}
